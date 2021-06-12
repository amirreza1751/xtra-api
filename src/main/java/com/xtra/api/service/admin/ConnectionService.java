package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.ConnectionMapper;
import com.xtra.api.model.line.BlockedIp;
import com.xtra.api.model.line.Connection;
import com.xtra.api.projection.admin.connection.BlockIpRequest;
import com.xtra.api.projection.admin.connection.ConnectionView;
import com.xtra.api.repository.BlockedIpRepository;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class ConnectionService extends CrudService<Connection, Long, ConnectionRepository> {
    private final ConnectionMapper connectionMapper;
    private final BlockedIpRepository blockedIpRepository;

    @Autowired
    public ConnectionService(ConnectionRepository repository, ConnectionMapper connectionMapper, BlockedIpRepository blockedIpRepository) {
        super(repository, "Connection");
        this.connectionMapper = connectionMapper;
        this.blockedIpRepository = blockedIpRepository;
    }

    public Page<ConnectionView> getActiveConnections(int pageNo, int pageSize, String sortBy, String sortDir) {
        return repository.findAll(getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(connectionMapper::convertToView);
    }

    @Transactional
    public void deleteOldConnections() {
        //delete connections not updated longer than one segment time
        repository.deleteAllByLastReadIsLessThanEqual(LocalDateTime.now().minusSeconds(10));
    }

    @Override
    protected Page<Connection> findWithSearch(String search, Pageable page) {
        return null;
    }

    public void blockIp(BlockIpRequest blockIpRequest) {
        var blockedIp = blockedIpRepository.findById(blockIpRequest.getIpAddress())
                .orElse(new BlockedIp(blockIpRequest.getIpAddress()));
        blockedIp.setUntil(blockIpRequest.getUntil());
        blockedIpRepository.save(blockedIp);
    }
}
