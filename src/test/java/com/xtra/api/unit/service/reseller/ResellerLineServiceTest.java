package com.xtra.api.unit.service.reseller;

import com.xtra.api.mapper.reseller.ResellerLineMapper;
import com.xtra.api.model.Line;
import com.xtra.api.model.Package;
import com.xtra.api.model.Reseller;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.admin.PackageService;
import com.xtra.api.service.reseller.ResellerLineServiceImpl;
import com.xtra.api.service.system.UserAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Period;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ResellerLineServiceTest {
    @Mock
    LineRepository repository;
    @Mock
    ResellerLineMapper lineMapper;
    @Mock
    ConnectionRepository connectionRepository;
    @Mock
    PackageService packageService;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @InjectMocks
    ResellerLineServiceImpl resellerLineService;

    @Test
    public void extendLine__() {
        var authService = Mockito.mockStatic(UserAuthService.class);
        authService.when(UserAuthService::getCurrentReseller).thenReturn(new Reseller());

        var pack = new Package();
        pack.setCredits(12);
        pack.setDuration(Period.of(0, 1, 0));
        pack.setMaxConnections(1);

        Line line = new Line();

        doReturn(pack).when(packageService).findByIdOrFail(anyLong());
        doReturn(line).when(resellerLineService).findByIdOrFail(anyLong());
        resellerLineService.extendLine(0L, 0L);

        doReturn(line).when(repository).save(line);
        //incomplete
    }
}
