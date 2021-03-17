package com.xtra.api.unit.service;

import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.admin.AdminLineServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private AdminLineServiceImpl lineService;

    @Test
    public void insert_noUsernameAndPassword_success() {

    }

}
