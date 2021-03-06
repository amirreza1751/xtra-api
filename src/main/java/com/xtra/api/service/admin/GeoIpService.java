package com.xtra.api.service.admin;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Service
public class GeoIpService {

    private DatabaseReader reader;

    @Autowired
    public GeoIpService(@Value("${maxmind.dbPath}") String dbPath) {
        File database = new File(System.getProperty("user.home") + dbPath);
        try {
            this.reader = new DatabaseReader.Builder(database).withCache(new CHMCache()).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<CityResponse> getIpInformation(String ip) {
        InetAddress ipAddress;
        Optional<CityResponse> response = Optional.empty();
        try {
            ipAddress = InetAddress.getByName(ip);
            response = reader.tryCity(ipAddress);
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
