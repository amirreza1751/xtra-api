package com.xtra.api.service.line;

import com.xtra.api.mapper.line.LineLineMapper;
import com.xtra.api.model.Line;
import com.xtra.api.projection.line.line.LineView;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Service
public class LineLineServiceImpl extends LineService {
    private final LineLineMapper lineMapper;

    @Autowired
    protected LineLineServiceImpl(LineRepository repository, LineLineMapper lineMapper, LineActivityRepository lineActivityRepository) {
        super(repository, Line.class, lineActivityRepository);
        this.lineMapper = lineMapper;
    }

    @Override
    protected Page<Line> findWithSearch(Pageable page, String search) {
        return null;
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findByIdOrFail(id));
    }

    public Map<String, String> downloadLine(Long id) {
        Map<String, String> data = new HashMap<>();

        String playlist = "test test test";
        data.put("fileName", "test.m3u8");
        data.put("playlist", playlist);

        return data;
    }
}
