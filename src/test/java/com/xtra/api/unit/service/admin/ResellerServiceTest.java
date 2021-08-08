package com.xtra.api.unit.service.admin;

import com.github.javafaker.Faker;
import com.xtra.api.projection.admin.user.reseller.ResellerInsertView;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.repository.ResellerRepository;
import com.xtra.api.service.admin.ResellerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ResellerServiceTest {

    @Autowired
    ResellerService resellerService;

    @MockBean
    private ResellerRepository resellerRepository;
    @Test
    public void createResellerTest() {
        Faker faker = new Faker();

        ResellerInsertView rv = new ResellerInsertView();
        rv.setUsername(faker.name().username());
        rv.setPassword(faker.letterify("??????"));
        rv.setEmail(faker.letterify("??????") + "@gmail.com");

        resellerService.add(rv);

        verify(resellerRepository.findByUsername(rv.getUsername()).isPresent());
    }
}
