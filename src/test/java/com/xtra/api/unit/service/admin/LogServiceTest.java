package com.xtra.api.unit.service.admin;

import com.xtra.api.model.line.Connection;
import com.xtra.api.service.LineService;
import com.xtra.api.service.admin.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class LogServiceTest {

    @Autowired
    LogService logService;

    @Qualifier("adminLineServiceImpl")
    @Autowired
    LineService lineService;

    @Test
    public void addConnectionToActivityLogTest() {
        List<Connection> connections = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            connections.add(new Connection());
        }
        this.logService.saveLogForConnections(connections);
    }
}
