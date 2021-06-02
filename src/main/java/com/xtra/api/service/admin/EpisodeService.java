package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.EpisodeMapper;
import com.xtra.api.model.Episode;
import com.xtra.api.projection.admin.episode.EpisodeListView;
import com.xtra.api.repository.EpisodeRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EpisodeService extends CrudService<Episode, Long, EpisodeRepository> {
    private final EpisodeMapper episodeMapper;

    protected EpisodeService(EpisodeRepository repository, EpisodeMapper episodeMapper) {
        super(repository, "Episode");
        this.episodeMapper = episodeMapper;
    }

    @Override
    protected Page<Episode> findWithSearch(String search, Pageable page) {
        return null;
    }

    public Page<EpisodeListView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(episodeMapper::convertToListView);
    }

}
