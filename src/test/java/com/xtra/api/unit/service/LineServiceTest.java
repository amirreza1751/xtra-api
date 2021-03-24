package com.xtra.api.unit.service;

import com.xtra.api.model.Line;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.LineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private LineActivityRepository lineActivityRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void lineInsert_noUsername_usernameGenerated() {
        Mockito.doReturn(false).when(lineRepository).existsByUsername(Mockito.anyString());

        LineService lineService = Mockito.mock(
                LineService.class, Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS).useConstructor(lineRepository, lineActivityRepository, bCryptPasswordEncoder)
        );
        Line line = new Line();
        line.setPassword("testPassword");
        Mockito.doReturn(line).when(lineRepository).save(Mockito.any(Line.class));
        var savedLine = lineService.insert(line);
        assertThat(savedLine.getUsername()).isNotNull();
    }

}
