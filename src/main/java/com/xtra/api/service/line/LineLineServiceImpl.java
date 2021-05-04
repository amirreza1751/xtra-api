package com.xtra.api.service.line;

import com.xtra.api.mapper.line.LineLineMapper;
import com.xtra.api.projection.line.line.LineInsertView;
import com.xtra.api.projection.line.line.LineView;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.xtra.api.service.system.UserAuthService.getCurrentLine;

@Service
public class LineLineServiceImpl extends LineService {
    private final LineLineMapper lineMapper;

    @Autowired
    protected LineLineServiceImpl(LineRepository repository, LineLineMapper lineMapper, ConnectionRepository connectionRepository
            , BCryptPasswordEncoder bCryptPasswordEncoder) {
        super(repository, connectionRepository, bCryptPasswordEncoder);
        this.lineMapper = lineMapper;
    }


    public LineView updateProfile(LineInsertView insertView) {
        return lineMapper.convertToView(updateOrFail(getCurrentLine().getId(), lineMapper.convertToEntity(insertView)));
    }

    public LineView getProfile() {
        return lineMapper.convertToView(getCurrentLine());
    }

    public ResponseEntity<String> downloadLinePlaylist() {
        return downloadLinePlaylist(getCurrentLine());
    }
}
