package com.xtra.api.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class GeoIpService {

    @Value("${maxmind.dbPath}")
    private String dbPath;

    private DatabaseReader reader;


    public GeoIpService(){
        File database = new File(System.getProperty("user.home") + dbPath);
        try {
            this.reader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Country getIpInformation(String ip){
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        InetAddress ipAddress = InetAddress.getByName("95.217.186.119");
        // Replace "city" with the appropriate method for your database, e.g.,
        // "country".
        CityResponse response = null;
        try {
            response = reader.city(ipAddress);
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
        }

        Country country = response != null ? response.getCountry() : null;
//        System.out.println("ISO Code: " + (country != null ? country.getIsoCode() : null));            // 'US'
//        System.out.println("" + (country != null ? country.getName() : null));               // 'United States'
//        System.out.println("" + country.getNames().get("MN")); // '美国'

        Subdivision subdivision = response != null ? response.getMostSpecificSubdivision() : null;
//        System.out.println(subdivision.getName());    // 'Minnesota'
//        System.out.println(subdivision.getIsoCode()); // 'MN'

        City city = response != null ? response.getCity() : null;
//        System.out.println(city.getName()); // 'Minneapolis'

        Postal postal = response != null ? response.getPostal() : null;
//        System.out.println(postal.getCode()); // '55455'

        Location location = response != null ? response.getLocation() : null;
//        System.out.println(location.getLatitude());  // 44.9733
//        System.out.println(location.getLongitude()); // -93.2323
        return country;
    }

}
