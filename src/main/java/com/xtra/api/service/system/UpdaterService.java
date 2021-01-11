package com.xtra.api.service.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.xtra.api.util.Utilities.decompressGzipFile;

@Service
public class UpdaterService {
    @Value("${maxmind.license-key}")
    private String maxmindLicenseKey;

    @Value("${temp-dir}")
    private String tempPath;

    @Value("${maxmind.dbPath}")
    private String dbPath;

    @Scheduled(initialDelay = 1000 * 10, fixedDelay = 1000 * 3600 * 24)
    public void updateMaxmindGeoIp() {
        String maxmindUrl = "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key=" + maxmindLicenseKey + "&suffix=tar.gz";
        WebClient webClient = WebClient.create(maxmindUrl);
        webClient.head().exchange().doOnSuccess(clientResponse -> {
            var tmp = clientResponse.headers().asHttpHeaders().get("Last-Modified");
            if (tmp != null) {
                var modifiedDate = tmp.get(0);
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

                try {
                    //Check if need to update geoIp
                    if (format.parse(modifiedDate).after(new Date())) {
                        //Download file
                        URL website = new URL(maxmindUrl);
                        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                        FileOutputStream fos = new FileOutputStream(tempPath + "geoIp-new.gz");
                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                        //@todo Check File Integrity

                        //Decompress gzip file
                        decompressGzipFile(tempPath + "geoIp-new.gz", tempPath + "geoIp-new");
                        File file = new File(tempPath + "geoIp-new" + "\\" + "GeoLite2-City.mmdb");
                        File dbFile = new File(dbPath);
                        if (!file.renameTo(dbFile)) {
                            System.out.println("could not update geoIp database");
                        }
                    } else
                        System.out.println("GeoIp is already the latest version");
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException exception) {
                    System.out.println("could not update geoIp database");
                }
            }

        }).block();
    }

}
