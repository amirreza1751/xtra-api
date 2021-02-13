package com.xtra.api;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.json.JSONObject;
import org.json.XML;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class EpgTest {

    @Test
    public void testEpgDataExtraction() {
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(xmlModule);
        URL xmlUrl = null;
        try {
            xmlUrl = new URL("http://epg.streamstv.me/hidden/guide-austria.xml");
            var xml=xmlUrl.getContent().toString();
            JSONObject xmlJSONObj = XML.toJSONObject(xml);

            System.out.println("s");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
