package com.xtra.api.unit;

import com.xtra.api.repository.EpgFileRepository;
import com.xtra.api.repository.ProgramRepository;
import org.json.JSONObject;
import org.json.XML;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("dev")
@DataJpaTest
public class EpgTest {

    @Autowired
    EpgFileRepository fileRepository;

    @Autowired
    ProgramRepository programRepository;

    @Test
    public void testEpgDataExtraction() {

        var xml = "";
        try {
            try (Scanner scanner = new Scanner(new URL("http://epg.streamstv.me/hidden/guide-austria.xml").openStream(),
                    StandardCharsets.UTF_8.toString())) {
                scanner.useDelimiter("\\A");
                xml += scanner.hasNext() ? scanner.next() : "";
            }
            JSONObject root = XML.toJSONObject(xml);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
