package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.EpisodeMapper;
import com.xtra.api.mapper.admin.SeasonMapper;
import com.xtra.api.model.exception.EntityAlreadyExistsException;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.vod.*;
import com.xtra.api.projection.admin.episode.*;
import com.xtra.api.projection.admin.video.AudioDetails;
import com.xtra.api.projection.admin.video.EncodeRequest;
import com.xtra.api.repository.EpisodeRepository;
import com.xtra.api.repository.SeriesRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class EpisodeService extends CrudService<Episode, Long, EpisodeRepository> {
    private final EpisodeMapper episodeMapper;
    private final SeasonMapper seasonMapper;
    private final SeriesRepository seriesRepository;
    private final SeriesService seriesService;
    private final ServerService serverService;

    protected EpisodeService(EpisodeRepository repository, EpisodeMapper episodeMapper, SeasonMapper seasonMapper, SeriesRepository seriesRepository, SeriesService seriesService, ServerService serverService) {
        super(repository, "Episode");
        this.episodeMapper = episodeMapper;
        this.seasonMapper = seasonMapper;
        this.seriesRepository = seriesRepository;
        this.seriesService = seriesService;
        this.serverService = serverService;
    }

    @Override
    protected Page<Episode> findWithSearch(String search, Pageable page) {
        return repository.findAllByEpisodeNameContains(search, page);
    }

    public Page<EpisodeListView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(episodeMapper::convertToListView);
    }

    public EpisodeView getViewById(Long id) {
        return episodeMapper.convertToView(findByIdOrFail(id));
    }

    public EpisodeView editEpisode(Long episodeId, EpisodeInsertView episodeInsertView) {
        var oldEpisode = findByIdOrFail(episodeId);
        Episode episodeToSave = episodeMapper.updateEntity(episodeInsertView, oldEpisode);

        var oldSeason = oldEpisode.getSeason();
        var series = oldSeason.getSeries();
        copyProperties(episodeToSave, oldEpisode, "id", "season", "videos", "servers");
        if (oldSeason.getSeasonNumber() == episodeInsertView.getSeason().getSeasonNumber()) {
            //replacing in the same season
            for (Episode episodeItem : oldSeason.getEpisodes()) {
                if (episodeToSave.getEpisodeNumber() == episodeItem.getEpisodeNumber() && !episodeId.equals(episodeItem.getId())) {
                    throw new EntityAlreadyExistsException();
                }
            }

        } else { // moving to another existing season
            oldSeason.getEpisodes().remove(oldEpisode);
            if (oldSeason.getEpisodes().isEmpty()) {
                series.getSeasons().remove(oldSeason);
            }
            var newSeason = series.getSeasons().stream().filter(season -> season.getSeasonNumber() == episodeInsertView.getSeason().getSeasonNumber()).findFirst();
            if (newSeason.isPresent()) {
                for (Episode episodeItem : newSeason.get().getEpisodes()) {
                    if (episodeToSave.getEpisodeNumber() == episodeItem.getEpisodeNumber() && !episodeId.equals(episodeItem.getId())) {
                        throw new EntityAlreadyExistsException();
                    }
                }
                List<Episode> episodes = newSeason.get().getEpisodes();
                episodes.add(oldEpisode);
                oldEpisode.setSeason(newSeason.get());
            } else {
                //new season has to be created
                var seasonToCreate = seasonMapper.convertToEntity(episodeInsertView.getSeason());
                List<Episode> episodes = new ArrayList<>();
                episodes.add(oldEpisode);
                seasonToCreate.setEpisodes(episodes);
                seasonToCreate.setSeries(series);
                oldEpisode.setSeason(seasonToCreate);
                List<Season> existingSeasons = series.getSeasons();
                existingSeasons.add(seasonToCreate);
                series.setSeasons(existingSeasons);
            }
        }
        seriesService.updateNumberOfEpisodes(series);
        updateSourceVideoInfo(oldEpisode.getVideo());
        seriesRepository.save(series);
        var result = seriesRepository.save(series).getSeasons().stream().map(season -> season.getEpisodes().stream().filter(episode -> episode.getId().equals(episodeId)).findFirst()).collect(Collectors.toList());
        return episodeMapper.convertToView(result.get(0).get());
    }

    public void deleteEpisode(Long episodeId) {
        var episode = findByIdOrFail(episodeId);
        var season = episode.getSeason();
        var series = season.getSeries();
        season.getEpisodes().remove(episode);
        if (season.getEpisodes().isEmpty()) {
            series.getSeasons().remove(season);
        }
        seriesService.updateNumberOfEpisodes(series);
        seriesRepository.save(series);
    }

    public void updateAll(EpisodeBatchUpdateView episodeBatchUpdateView) {
        var episodeIds = episodeBatchUpdateView.getEpisodeIds();
        var serverIds = episodeBatchUpdateView.getServerIds();

        if (episodeIds != null) {
            for (Long episodeId : episodeIds) {
                var episode = repository.findById(episodeId).orElseThrow(() -> new EntityNotFoundException("Episode", episodeId.toString()));

                if (serverIds.size() > 0) {
                    var video = episode.getVideo();
                    Set<VideoServer> videoServers = serverIds.stream().map(serverId -> {
                        var videoServer = new VideoServer();
                        var server = serverService.findByIdOrFail(serverId);
                        videoServer.setServer(server);
                        videoServer.setVideo(video);
                        return videoServer;
                    }).collect(Collectors.toSet());

                    if (!episodeBatchUpdateView.isKeepServers()) {
                        video.getVideoServers().retainAll(videoServers);
                    }
                    video.getVideoServers().addAll(videoServers);

                }
                repository.save(episode);
            }
        }
    }

    public void deleteAll(EpisodeBatchDeleteView episodeBatchDeleteView) {
        var episodeIds = episodeBatchDeleteView.getEpisodeIds();
        if (episodeIds != null) {
            for (Long episodeId : episodeIds) {
                deleteEpisode(episodeId);
            }
        }
    }

    public void encode(Long id) {
        var episode = findByIdOrFail(id);
        var videoServers = episode.getVideo().getVideoServers();
        for (var videoServer : videoServers) {
            serverService.sendEncodeRequest(videoServer.getServer(), createEncodeRequest(episode.getVideo()));
        }
    }

    private void updateSourceVideoInfo(Video video) {
        video.setSourceVideoInfo(episodeMapper.toVideoInfo(serverService.getMediaInfo(video.getSourceServer(), video.getSourceLocation())));
    }

    private EncodeRequest createEncodeRequest(Video video) {
        var encodeRequest = new EncodeRequest();
        encodeRequest.setVideoId(video.getId());
        encodeRequest.setSourceLocation(video.getSourceLocation());
        encodeRequest.setSourceAudios(video.getSourceAudios().stream().map(audio -> new AudioDetails(audio.getLocation(), audio.getLanguage())).collect(Collectors.toList()));
        encodeRequest.setTargetResolutions(video.getTargetResolutions());
        encodeRequest.setTargetAudioCodec(AudioCodec.AAC);
        encodeRequest.setTargetVideoCodec(VideoCodec.H264);
        return encodeRequest;
    }

    public Episode findBySeriesIdSeasonNumberEpisodeNumber(Long id, int season_number, int episode_number) {
        return repository.findBySeason_SeriesIdAndSeason_SeasonNumberAndEpisodeNumber(id, season_number, episode_number);
    }
}
