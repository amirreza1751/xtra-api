package com.xtra.api.service.admin;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

@Service
@Log4j2
public class GeoIpService {

    private DatabaseReader reader;

    @Autowired
    public GeoIpService(@Value("${maxmind.dbPath}") String dbPath) {
        File database = new File(dbPath);
        try {
            this.reader = new DatabaseReader.Builder(database).withCache(new CHMCache()).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<CityResponse> getIpInformation(String ip) {
        InetAddress ipAddress;
        Optional<CityResponse> response;
        try {
            ipAddress = InetAddress.getByName(ip);
            response = reader.tryCity(ipAddress);
        } catch (IOException | GeoIp2Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
        return response;
    }

}
