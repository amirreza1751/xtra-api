package com.xtra.api.service.admin;

import com.xtra.api.exception.DuplicateEpisodesException;
import com.xtra.api.mapper.admin.EpisodeMapper;
import com.xtra.api.mapper.admin.SeasonMapper;
import com.xtra.api.mapper.admin.SeriesMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import com.xtra.api.projection.admin.movie.MovieInsertView;
import com.xtra.api.projection.admin.series.SeriesInsertView;
import com.xtra.api.projection.admin.series.SeriesView;
import com.xtra.api.repository.CollectionVodRepository;
import com.xtra.api.repository.SeriesRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class SeriesService extends CrudService<Series, Long, SeriesRepository> {

    private final SeriesMapper seriesMapper;
    private final CollectionVodRepository collectionVodRepository;
    private final EpisodeMapper episodeMapper;
    private final SeasonMapper seasonMapper;

    @Autowired
    protected SeriesService(SeriesRepository repository, SeriesMapper seriesMapper, CollectionVodRepository collectionVodRepository, EpisodeMapper episodeMapper, SeasonMapper seasonMapper) {
        super(repository, "Series");
        this.seriesMapper = seriesMapper;
        this.collectionVodRepository = collectionVodRepository;
        this.episodeMapper = episodeMapper;
        this.seasonMapper = seasonMapper;
    }

    @Override
    protected Page<Series> findWithSearch(String search, Pageable page) {
        return null;
    }

    public Series add(SeriesInsertView seriesInsertView){
        return this.insert(seriesMapper.convertToEntity(seriesInsertView));
    }

    public Series insert(Series series){
        return repository.save(series);
    }

    public SeriesView save(Long id, SeriesInsertView seriesInsertView){
        return seriesMapper.convertToView(this.update(id, seriesMapper.convertToEntity(seriesInsertView)));
    }

    public Series update(Long id, Series series) {
        var oldSeries = findByIdOrFail(id);
        copyProperties(series, oldSeries, "id", "collectionAssigns", "seasons");
        if (series.getCollectionAssigns() != null) {
            collectionVodRepository.deleteAll();
            oldSeries.getCollectionAssigns().addAll(series.getCollectionAssigns().stream().peek(collectionVod -> {
                collectionVod.setId(new CollectionVodId(collectionVod.getCollection().getId(), oldSeries.getId()));
                collectionVod.setVod(oldSeries);
            }).collect(Collectors.toSet()));
        }
        return repository.save(oldSeries);
    }
    public void deleteSeries(Long id){
        var seriesToDelete = findByIdOrFail(id);
        if (seriesToDelete.getCollectionAssigns() != null) {
            collectionVodRepository.deleteAll();
        }
        repository.delete(seriesToDelete);
    }

    //Episodes Section
    public Series addEpisode (Long id, EpisodeInsertView episodeInsertView){
        Episode episode = episodeMapper.convertToEntity(episodeInsertView);
        var series = findByIdOrFail(id);
            var season = series.getSeasons().stream().filter(seasonItem -> seasonItem.getSeasonNumber() == episodeInsertView.getSeason().getSeasonNumber()).findFirst();
            if (season.isPresent()){
                var existingEpisode = season.get().getEpisodes().stream().filter(episodeItem -> episodeItem.getEpisodeNumber() == episodeInsertView.getEpisodeNumber()).findFirst();
                if (existingEpisode.isPresent()){
                    throw new DuplicateEpisodesException(episode.getEpisodeName(), episode.getEpisodeNumber());
                } else {
                    List<Episode> existingEpisodes = season.get().getEpisodes();
                    existingEpisodes.add(episode);
                    return repository.save(series);
                }
            } else {
                    var newSeason = seasonMapper.convertToEntity(episodeInsertView.getSeason());
                    List<Episode> episodes = new ArrayList<>();
                    episodes.add(episode);
                    newSeason.setEpisodes(episodes);
                    List<Season> existingSeasons = series.getSeasons();
                    existingSeasons.add(newSeason);
                    series.setSeasons(existingSeasons);
                    repository.save(series);
            }
        return series;
    }
}
