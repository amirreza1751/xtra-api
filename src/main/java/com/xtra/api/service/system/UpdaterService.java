package com.xtra.api.service.system;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.xtra.api.util.Utilities.decompressGzipFile;

@Service
@Log4j2
public class UpdaterService {
    @Value("${maxmind.license-key}")
    private String maxmindLicenseKey;

    @Value("${temp-dir}")
    private String tempDir;

    @Value("${maxmind.dbPath}")
    private String dbPath;

    private String maxmindUrl;

    @PostConstruct
    public void init() {
        this.maxmindUrl = String.format("https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key=%s&suffix=tar.gz", maxmindLicenseKey);
    }

    @Scheduled(initialDelay = 1000 * 10, fixedDelay = 1000 * 3600 * 24)
    public void updateMaxmindGeoIp() {
        WebClient webClient = WebClient.create(maxmindUrl);
        webClient.head().retrieve().toBodilessEntity().map(HttpEntity::getHeaders).subscribe(headers ->
                {
                    var tmp = headers.get("Last-Modified");
                    if (tmp != null) {
                        var modifiedDate = tmp.get(0);
                        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

                        try {
                            //Check if db does not exists
                            File databaseDir = new File(dbPath);
                            if (!databaseDir.exists()) {
                                if (!databaseDir.getParentFile().mkdirs() && !databaseDir.getParentFile().exists()) {
                                    log.error("Could not create directory " + databaseDir.getParentFile() + " for database");
                                } else {
                                    downloadNewDatabase();
                                }
                            } else {
                                //Check if need to update geoIp
                                if (format.parse(modifiedDate).after(new Date(databaseDir.lastModified()))) {
                                    downloadNewDatabase();
                                } else
                                    log.info("GeoIp is already the latest version");
                            }
                        } catch (ParseException e) {
                            log.error("Error in reading new database headers");
                        }
                    }
                }
        );
    }

    private void downloadNewDatabase() {
        try {
            //Download file
            log.info("Downloading new GeoIp database...");
            URL website = new URL(maxmindUrl);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());

            FileOutputStream fos = new FileOutputStream(tempDir + "/geoIp-new.gz");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            //@todo Check File Integrity

            //Decompress gzip file
            decompressGzipFile(tempDir + "/geoIp-new.gz", tempDir + "/geoIp-new");
            Files.move(Path.of(tempDir + "/geoIp-new"), Path.of(dbPath), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            log.error("Error while updating geoip database");
            log.error(e.getMessage());
        }

    }

}
