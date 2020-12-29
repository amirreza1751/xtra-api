package com.xtra.api.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("dev")
@DataJpaTest
class GeoIpServiceTest {

    private DatabaseReader reader;

    @Autowired
    private GeoIpService geoIpService;

    @BeforeEach
    void setUp() throws IOException {
        // A File object pointing to your GeoIP2 or GeoLite2 database
        File database = new File(System.getProperty("user.home") + File.separator + "/workspace/xtra/xtra2/GeoLite2-City_20201229/GeoLite2-City.mmdb");

        // This creates the DatabaseReader object. To improve performance, reuse
        // the object across lookups. The object is thread-safe.
        reader = new DatabaseReader.Builder(database).build();
    }

    @Test
    void shouldGetIpInformation() throws IOException, GeoIp2Exception {
        Country country = geoIpService.getIpInformation("128.101.101.101");
        System.out.println("ISO Code: " + country.getName());


//        InetAddress ipAddress = InetAddress.getByName("128.101.101.101");
////        InetAddress ipAddress = InetAddress.getByName("95.217.186.119");
//        // Replace "city" with the appropriate method for your database, e.g.,
//        // "country".
//        CityResponse response = reader.city(ipAddress);
//
//        Country country = response.getCountry();
//        System.out.println("ISO Code: " + country.getIsoCode());            // 'US'
//        System.out.println("" + country.getName());               // 'United States'
//        System.out.println("" + country.getNames().get("MN")); // '美国'
//
//        Subdivision subdivision = response.getMostSpecificSubdivision();
//        System.out.println(subdivision.getName());    // 'Minnesota'
//        System.out.println(subdivision.getIsoCode()); // 'MN'
//
//        City city = response.getCity();
//        System.out.println(city.getName()); // 'Minneapolis'
//
//        Postal postal = response.getPostal();
//        System.out.println(postal.getCode()); // '55455'
//
//        Location location = response.getLocation();
//        System.out.println(location.getLatitude());  // 44.9733
//        System.out.println(location.getLongitude()); // -93.2323
    }
}