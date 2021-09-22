package com.xtra.api.service.admin;

import com.xtra.api.model.collection.CollectionVod;
import com.xtra.api.model.collection.CollectionVodId;
import com.xtra.api.model.exception.EntityAlreadyExistsException;
import com.xtra.api.mapper.admin.EpisodeMapper;
import com.xtra.api.mapper.admin.SeasonMapper;
import com.xtra.api.mapper.admin.SeriesMapper;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.vod.*;
import com.xtra.api.projection.admin.episode.EpisodeImport;
import com.xtra.api.projection.admin.episode.EpisodeImportView;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import com.xtra.api.projection.admin.movie.MovieImport;
import com.xtra.api.projection.admin.series.SeriesBatchDeleteView;
import com.xtra.api.projection.admin.series.SeriesBatchUpdateView;
import com.xtra.api.projection.admin.series.SeriesInsertView;
import com.xtra.api.projection.admin.series.SeriesView;
import com.xtra.api.repository.CollectionVodRepository;
import com.xtra.api.repository.SeriesRepository;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.service.CrudService;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbTvEpisodes;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.tv.TvEpisode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.generateRandomString;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class SeriesService extends CrudService<Series, Long, SeriesRepository> {

    private final SeriesMapper seriesMapper;
    private final CollectionVodRepository collectionVodRepository;
    private final EpisodeMapper episodeMapper;
    private final SeasonMapper seasonMapper;
    private final VideoRepository videoRepository;
    private final ServerService serverService;

    @Autowired
    protected SeriesService(SeriesRepository repository,
                            SeriesMapper seriesMapper,
                            CollectionVodRepository collectionVodRepository,
                            EpisodeMapper episodeMapper,
                            SeasonMapper seasonMapper,
                            VideoRepository videoRepository, ServerService serverService) {
        super(repository, "Series");
        this.seriesMapper = seriesMapper;
        this.collectionVodRepository = collectionVodRepository;
        this.episodeMapper = episodeMapper;
        this.seasonMapper = seasonMapper;
        this.videoRepository = videoRepository;
        this.serverService = serverService;
    }

    @Override
    protected Page<Series> findWithSearch(String search, Pageable page) {

        return repository.findAllByNameContainsOrInfoPlotContainsOrInfoCastContainsOrInfoDirectorContainsOrInfoGenresContainsOrInfoCountryContains(search, search, search, search, search, search, page);
    }

    public Page<SeriesView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(seriesMapper::convertToView);
    }

    public SeriesView getViewById(Long id) {
        return seriesMapper.convertToView(findByIdOrFail(id));
    }

    public SeriesView add(SeriesInsertView seriesInsertView) {
        seriesInsertView.setLastUpdated(LocalDate.now());
        return seriesMapper.convertToView(this.insert(seriesMapper.convertToEntity(seriesInsertView)));
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
            List<CollectionVod> collectionVodListToDelete = new ArrayList<>(oldSeries.getCollectionAssigns());
            collectionVodRepository.deleteInBatch(collectionVodListToDelete);
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

    public void updateAll(SeriesBatchUpdateView seriesBatchUpdateView) {
        var seriesIds = seriesBatchUpdateView.getSeriesIds();
        var collectionIds = seriesBatchUpdateView.getCollectionIds();

        if (seriesIds != null) {
            for (Long seriesId : seriesIds) {
                var series = repository.findById(seriesId).orElseThrow(() -> new EntityNotFoundException("Series", seriesId.toString()));

                if (collectionIds.size() > 0) {
                    Set<CollectionVod> collectionVodSet = seriesMapper.convertToCollections(collectionIds, series);
                    if (!seriesBatchUpdateView.isKeepCollections())
                        series.getCollectionAssigns().retainAll(collectionVodSet);
                    series.getCollectionAssigns().addAll(collectionVodSet);
                }
                repository.save(series);
            }
        }
    }

    public void deleteAll(SeriesBatchDeleteView seriesBatchDeleteView) {
        var seriesIds = seriesBatchDeleteView.getSeriesIds();
        if (seriesIds != null) {
            for (Long seriesId : seriesIds) {
                deleteSeries(seriesId);
            }
        }
    }

    //Episodes Section
    public SeriesView addEpisode(Long id, EpisodeInsertView episodeInsertView) {
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
                ExecutorService executor = Executors.newFixedThreadPool(1);
                executor.execute(() -> {
                    updateVideoInfo(episode.getVideos());
                    repository.save(series);
                });
                executor.shutdown();
                return seriesMapper.convertToView(repository.save(series));
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
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(() -> {
                updateVideoInfo(episode.getVideos());
                repository.save(series);
            });
            executor.shutdown();
            return seriesMapper.convertToView(repository.save(series));
        }
    }

    public void importEpisodes(Long id, EpisodeImportView importView) {
        var servers = importView.getServers();
        var episodes = importView.getEpisodes();

        for (EpisodeImport episode : episodes) {
            EpisodeInsertView insertView = new EpisodeInsertView();
            insertView.setEpisodeNumber(episode.getEpisodeNumber());
            insertView.setEpisodeName(episode.getEpisodeName());
            insertView.setNotes(importView.getNotes());

            TmdbTvEpisodes tvEpisodes = new TmdbApi("0edee3d3e5acd5c5a46d304175c0166e").getTvEpisodes();
            TvEpisode episodeInfo = tvEpisodes.getEpisode(episode.getTmdbId(), episode.getEpisodeNumber(), episode.getEpisodeNumber(),"en", TmdbTvEpisodes.EpisodeMethod.credits, TmdbTvEpisodes.EpisodeMethod.videos);

            insertView.setSeason(episode.getSeason());
            insertView.setVideos(episode.getVideos());
            insertView.setServers(servers);

            addEpisode(id, insertView);
        }
    }

    public void generateToken(Episode episode) {
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

    public void updateVideoInfo(Set<Video> videoSet) {
        var videoInfoList = serverService.getMediaInfo(videoSet.iterator().next().getVideoServers().iterator().next().getServer(), new ArrayList<>(videoSet));
        Iterator<Video> videosIterator = videoSet.iterator();
        Iterator<VideoInfo> videoInfosIterator = videoInfoList.iterator();
        while (videoInfosIterator.hasNext() && videosIterator.hasNext()) {
            videosIterator.next().setVideoInfo(videoInfosIterator.next());
        }
    }
}
