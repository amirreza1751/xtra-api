package com.xtra.api.service.admin;

import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.mapper.admin.ConnectionMapper;
import com.xtra.api.model.Connection;
import com.xtra.api.projection.admin.ConnectionView;
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

    @Autowired
    public ConnectionService(ConnectionRepository repository, ConnectionMapper connectionMapper) {
        super(repository, "Connection");
        this.connectionMapper = connectionMapper;
    }

    public Page<ConnectionView> getActiveConnections(int pageNo, int pageSize, String sortBy, String sortDir) {
        return repository.findAllByKilledFalse(getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(connectionMapper::convertToView);
    }

    public void endConnection(Long id) {
        var connection = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        connection.setKilled(true);
        repository.save(connection);
    }

    @Transactional
    public void deleteOldConnections() {
        repository.deleteAllByKilledTrueAndEndDateBefore(LocalDateTime.now().minusMinutes(1));
        //delete connections not updated longer than one segment time
        repository.deleteAllByKilledFalseAndLastReadIsLessThanEqual(LocalDateTime.now().minusSeconds(10));
    }

    @Override
    protected Page<Connection> findWithSearch(String search, Pageable page) {
        return null;
    }
}
