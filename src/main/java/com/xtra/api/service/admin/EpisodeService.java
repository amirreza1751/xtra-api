package com.xtra.api.service.admin;

import com.xtra.api.exception.EntityAlreadyExistsException;
import com.xtra.api.mapper.admin.EpisodeMapper;
import com.xtra.api.mapper.admin.SeasonMapper;
import com.xtra.api.model.Episode;
import com.xtra.api.model.Season;
import com.xtra.api.model.Series;
import com.xtra.api.projection.admin.channel.ChannelView;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import com.xtra.api.projection.admin.episode.EpisodeListView;
import com.xtra.api.projection.admin.episode.EpisodeView;
import com.xtra.api.repository.EpisodeRepository;
import com.xtra.api.repository.SeriesRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class EpisodeService extends CrudService<Episode, Long, EpisodeRepository> {
    private final EpisodeMapper episodeMapper;
    private final SeasonMapper seasonMapper;
    private final SeriesRepository seriesRepository;
    private final SeriesService seriesService;

    protected EpisodeService(EpisodeRepository repository, EpisodeMapper episodeMapper, SeasonMapper seasonMapper, SeriesRepository seriesRepository, SeriesService seriesService) {
        super(repository, "Episode");
        this.episodeMapper = episodeMapper;
        this.seasonMapper = seasonMapper;
        this.seriesRepository = seriesRepository;
        this.seriesService = seriesService;
    }

    @Override
    protected Page<Episode> findWithSearch(String search, Pageable page) {
        return null;
    }

    public Page<EpisodeListView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(episodeMapper::convertToListView);
    }

    public EpisodeView getViewById(Long id) {
        return episodeMapper.convertToView(findByIdOrFail(id));
    }

    public Series editEpisode(Long episodeId, EpisodeInsertView episodeInsertView) {
        Episode episodeToSave = episodeMapper.convertToEntity(episodeInsertView);
        var oldEpisode = findByIdOrFail(episodeId);
        var oldSeason = oldEpisode.getSeason();
        var series = oldSeason.getSeries();

        if (oldSeason.getSeasonNumber() == episodeInsertView.getSeason().getSeasonNumber()) { //replacing in the same season
            for (Episode episodeItem : oldSeason.getEpisodes()){
                if (episodeToSave.getEpisodeNumber() == episodeItem.getEpisodeNumber() && !episodeId.equals(episodeItem.getId())){
                    throw new EntityAlreadyExistsException();
                }
            }
            copyProperties(episodeToSave, oldEpisode, "id", "season", "videos");
//            oldEpisode.getVideos().retainAll(episodeToSave.getVideos());
//            oldEpisode.getVideos().addAll(episodeToSave.getVideos());
//            oldEpisode.getVideos().clear();
//            oldEpisode.setVideos(episodeToSave.getVideos());
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
                episodes.add(episodeToSave);
            } else { //new season has to be created
                var seasonToCreate = seasonMapper.convertToEntity(episodeInsertView.getSeason());
                List<Episode> episodes = new ArrayList<>();
                episodes.add(episodeToSave);
                seasonToCreate.setEpisodes(episodes);
                List<Season> existingSeasons = series.getSeasons();
                existingSeasons.add(seasonToCreate);
                series.setSeasons(existingSeasons);
            }
        }
        seriesService.updateNumberOfEpisodes(series);
        return seriesRepository.save(series);
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
}
