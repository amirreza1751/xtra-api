package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.VodConnectionMapper;
import com.xtra.api.model.line.BlockedIp;
import com.xtra.api.model.line.VodConnection;
import com.xtra.api.projection.admin.connection.BlockIpRequest;
import com.xtra.api.projection.admin.connection.ConnectionView;
import com.xtra.api.repository.BlockedIpRepository;
import com.xtra.api.repository.VodConnectionRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class VodConnectionService extends CrudService<VodConnection, Long, VodConnectionRepository> {
    private final VodConnectionMapper vodConnectionMapper;
    private final BlockedIpRepository blockedIpRepository;
    private final LogService logService;

    @Autowired
    public VodConnectionService(VodConnectionRepository repository, VodConnectionMapper vodConnectionMapper,
                                BlockedIpRepository blockedIpRepository, LogService logService) {
        super(repository, "VodConnection");
        this.vodConnectionMapper = vodConnectionMapper;
        this.blockedIpRepository = blockedIpRepository;
        this.logService = logService;
    }

    public Page<ConnectionView> getActiveConnections(int pageNo, int pageSize, String sortBy, String sortDir) {
        return repository.findAll(getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(vodConnectionMapper::convertToView);
    }

    @Transactional
    public void deleteOldConnections() {
        //save activityLog for abandoned connections
        var vodConnections = repository.findAllByLastReadLessThanEqual(LocalDateTime.now().minusSeconds(20));
        if (vodConnections.size() > 0) {
//            logService.saveLogForConnections(connections);
            //delete connections not updated longer than one segment time
            repository.deleteAll(vodConnections);
        }

    }

    @Override
    protected Page<VodConnection> findWithSearch(String search, Pageable page) {
        return null;
    }

    public void blockIp(BlockIpRequest blockIpRequest) {
        var blockedIp = blockedIpRepository.findById(blockIpRequest.getIpAddress())
                .orElse(new BlockedIp(blockIpRequest.getIpAddress()));
        blockedIp.setUntil(blockIpRequest.getUntil());
        blockedIpRepository.save(blockedIp);
    }
}
