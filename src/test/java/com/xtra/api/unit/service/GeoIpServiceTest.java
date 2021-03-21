package com.xtra.api.unit.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.xtra.api.service.admin.GeoIpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations="classpath:application-dev.properties")
@DataJpaTest
class GeoIpServiceTest {


    @MockBean
    private DatabaseReader reader;

    @MockBean
    private final String dbPath = "/workspace/xtra/xtra2/GeoLite2-City_20201229/GeoLite2-City.mmdb";

    @InjectMocks
    private static GeoIpService geoIpService;

    @BeforeEach
    void setUp() throws IOException {
        // A File object pointing to your GeoIP2 or GeoLite2 database
//        File database = new File(System.getProperty("user.home") + File.separator + "/workspace/xtra/xtra2/GeoLite2-City_20201229/GeoLite2-City.mmdb");

        // This creates the DatabaseReader object. To improve performance, reuse
        // the object across lookups. The object is thread-safe.
//        reader = new DatabaseReader.Builder(database).build();
    }

    @Test
    void shouldGetIpInformation() throws IOException, GeoIp2Exception {
//        CityResponse country = geoIpService.getIpInformation("128.101.101.101");
    }
}