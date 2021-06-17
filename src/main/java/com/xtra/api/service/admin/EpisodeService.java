package com.xtra.api.service.admin;

import com.xtra.api.model.exception.EntityAlreadyExistsException;
import com.xtra.api.mapper.admin.EpisodeMapper;
import com.xtra.api.mapper.admin.SeasonMapper;
import com.xtra.api.model.vod.EncodeStatus;
import com.xtra.api.model.vod.Episode;
import com.xtra.api.model.vod.Season;
import com.xtra.api.model.vod.Video;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import com.xtra.api.projection.admin.episode.EpisodeListView;
import com.xtra.api.projection.admin.episode.EpisodeView;
import com.xtra.api.repository.EpisodeRepository;
import com.xtra.api.repository.SeriesRepository;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.generateRandomString;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class EpisodeService extends CrudService<Episode, Long, EpisodeRepository> {
    private final EpisodeMapper episodeMapper;
    private final SeasonMapper seasonMapper;
    private final SeriesRepository seriesRepository;
    private final SeriesService seriesService;
    private final VideoRepository videoRepository;

    protected EpisodeService(EpisodeRepository repository, EpisodeMapper episodeMapper, SeasonMapper seasonMapper, SeriesRepository seriesRepository, SeriesService seriesService, VideoRepository videoRepository) {
        super(repository, "Episode");
        this.episodeMapper = episodeMapper;
        this.seasonMapper = seasonMapper;
        this.seriesRepository = seriesRepository;
        this.seriesService = seriesService;
        this.videoRepository = videoRepository;
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
        Episode episodeToSave = episodeMapper.convertToEntity(episodeInsertView);
        var oldEpisode = findByIdOrFail(episodeId);
        var oldSeason = oldEpisode.getSeason();
        var series = oldSeason.getSeries();
        copyProperties(episodeToSave, oldEpisode, "id", "season", "videos", "servers");
        if (oldSeason.getSeasonNumber() == episodeInsertView.getSeason().getSeasonNumber()) { //replacing in the same season
            for (Episode episodeItem : oldSeason.getEpisodes()){
                if (episodeToSave.getEpisodeNumber() == episodeItem.getEpisodeNumber() && !episodeId.equals(episodeItem.getId())){
                    throw new EntityAlreadyExistsException();
                }
            }
            oldEpisode.getVideos().retainAll(episodeToSave.getVideos());
            List<Video> videosToAdd = new ArrayList<>();
            for (Video video : episodeToSave.getVideos()){
                var target = oldEpisode.getVideos().stream().filter(videoItem -> videoItem.equals(video)).findFirst();
                if (target.isPresent()){
                    copyProperties(video, target, "id", "token", "encodeStatus", "videoInfo", "videoServers");
                    target.get().getAudios().clear();
                    target.get().getAudios().addAll(video.getAudios());
                    target.get().getSubtitles().clear();
                    target.get().getSubtitles().addAll(video.getSubtitles());
                } else {
                    videosToAdd.add(video);
                }
            }
            for (Video video : videosToAdd){
                video.setId(null);
                this.generateToken(video);
            }
            oldEpisode.getVideos().addAll(videosToAdd);
        } else { // moving to another existing season
            oldSeason.getEpisodes().remove(oldEpisode);
            if (oldSeason.getEpisodes().isEmpty()){
                series.getSeasons().remove(oldSeason);
            }
            var newSeason = series.getSeasons().stream().filter(season -> season.getSeasonNumber() == episodeInsertView.getSeason().getSeasonNumber()).findFirst();
            if (newSeason.isPresent()) {
                for (Episode episodeItem : newSeason.get().getEpisodes()){
                    if (episodeToSave.getEpisodeNumber() == episodeItem.getEpisodeNumber() && !episodeId.equals(episodeItem.getId())){
                        throw new EntityAlreadyExistsException();
                    }
                }
                List<Episode> episodes = newSeason.get().getEpisodes();
                episodes.add(oldEpisode);
                oldEpisode.setSeason(newSeason.get());
            } else { //new season has to be created
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
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {
            seriesService.updateVideoInfo(oldEpisode.getVideos());
            seriesRepository.save(series);
        });
        executor.shutdown();
        var result = seriesRepository.save(series).getSeasons().stream().map(season -> season.getEpisodes().stream().filter(episode -> episode.getId().equals(episodeId)).findFirst()).collect(Collectors.toList());
        return episodeMapper.convertToView(result.get(0).get());
    }

    public void deleteEpisode(Long episodeId){
        var episode = findByIdOrFail(episodeId);
        var season = episode.getSeason();
        var series = season.getSeries();
        season.getEpisodes().remove(episode);
        if (season.getEpisodes().isEmpty()){
            series.getSeasons().remove(season);
        }
        seriesService.updateNumberOfEpisodes(series);
        seriesRepository.save(series);
    }
    public void generateToken(Video video){
        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (videoRepository.findByToken(token).isPresent());
        video.setToken(token);
        video.setEncodeStatus(EncodeStatus.NOT_ENCODED);
    }
}
