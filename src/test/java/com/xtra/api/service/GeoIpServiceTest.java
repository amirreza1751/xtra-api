package com.xtra.api.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

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