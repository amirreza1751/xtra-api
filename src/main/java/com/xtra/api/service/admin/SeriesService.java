package com.xtra.api.service.admin;

import com.xtra.api.model.collection.CollectionVodId;
import com.xtra.api.model.exception.EntityAlreadyExistsException;
import com.xtra.api.mapper.admin.EpisodeMapper;
import com.xtra.api.mapper.admin.SeasonMapper;
import com.xtra.api.mapper.admin.SeriesMapper;
import com.xtra.api.model.vod.*;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import com.xtra.api.projection.admin.series.SeriesInsertView;
import com.xtra.api.projection.admin.series.SeriesView;
import com.xtra.api.repository.CollectionVodRepository;
import com.xtra.api.repository.SeriesRepository;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.generateRandomString;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class SeriesService extends CrudService<Series, Long, SeriesRepository> {

    private final SeriesMapper seriesMapper;
    private final CollectionVodRepository collectionVodRepository;
    private final EpisodeMapper episodeMapper;
    private final SeasonMapper seasonMapper;
    private final VideoRepository videoRepository;

    @Autowired
    protected SeriesService(SeriesRepository repository,
                            SeriesMapper seriesMapper,
                            CollectionVodRepository collectionVodRepository,
                            EpisodeMapper episodeMapper,
                            SeasonMapper seasonMapper,
                            VideoRepository videoRepository) {
        super(repository, "Series");
        this.seriesMapper = seriesMapper;
        this.collectionVodRepository = collectionVodRepository;
        this.episodeMapper = episodeMapper;
        this.seasonMapper = seasonMapper;
        this.videoRepository = videoRepository;
    }

    @Override
    protected Page<Series> findWithSearch(String search, Pageable page) {
        return null;
    }

    public Page<SeriesView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(seriesMapper::convertToView);
    }

    public SeriesView getViewById(Long id) {
        return seriesMapper.convertToView(findByIdOrFail(id));
    }

    public Series add(SeriesInsertView seriesInsertView) {
        seriesInsertView.setLastUpdated(LocalDate.now());
        return this.insert(seriesMapper.convertToEntity(seriesInsertView));
    }

    public Series insert(Series series) {
        return repository.save(series);
    }

    public SeriesView save(Long id, SeriesInsertView seriesInsertView) {
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

    public void deleteSeries(Long id) {
        var seriesToDelete = findByIdOrFail(id);
        if (seriesToDelete.getCollectionAssigns() != null) {
            collectionVodRepository.deleteAll(seriesToDelete.getCollectionAssigns());
        }
        repository.delete(seriesToDelete);
    }

    //Episodes Section
    public Series addEpisode(Long id, EpisodeInsertView episodeInsertView) {
        Episode episode = episodeMapper.convertToEntity(episodeInsertView);
        var series = findByIdOrFail(id);
        series.setLastUpdated(LocalDate.now());
        var season = series.getSeasons().stream().filter(seasonItem -> seasonItem.getSeasonNumber() == episodeInsertView.getSeason().getSeasonNumber()).findFirst();
        if (season.isPresent()) {
            var existingEpisode = season.get().getEpisodes().stream().filter(episodeItem -> episodeItem.getEpisodeNumber() == episodeInsertView.getEpisodeNumber()).findFirst();
            if (existingEpisode.isPresent()) {
                throw new EntityAlreadyExistsException(episode.getEpisodeName(), episode.getEpisodeNumber());
            } else {
                this.generateToken(episode);
                episode.setSeason(season.get());
                List<Episode> existingEpisodes = season.get().getEpisodes();
                existingEpisodes.add(episode);
                season.get().setSeries(series);
                this.updateNumberOfEpisodes(series);
                return repository.save(series);
            }
        } else {
            this.generateToken(episode);
            var newSeason = seasonMapper.convertToEntity(episodeInsertView.getSeason());
            episode.setSeason(newSeason);
            List<Episode> episodes = new ArrayList<>();
            episodes.add(episode);
            newSeason.setEpisodes(episodes);
            newSeason.setSeries(series);
            List<Season> existingSeasons = series.getSeasons();
            existingSeasons.add(newSeason);
            series.setSeasons(existingSeasons);
            this.updateNumberOfEpisodes(series);
            return repository.save(series);
        }
    }

    public void generateToken(Episode episode){
        String token;
        for (Video video : episode.getVideos()){
            do {
                token = generateRandomString(8, 12, false);
            } while (videoRepository.findByToken(token).isPresent());
            video.setToken(token);
            video.setEncodeStatus(EncodeStatus.NOT_ENCODED);
        }
    }
    public void updateNumberOfEpisodes(Series series){
        if (series.getSeasons() != null){
            for (Season season : series.getSeasons()){
                season.setNoOfEpisodes(season.getEpisodes().size());
            }
        }
    }
}
