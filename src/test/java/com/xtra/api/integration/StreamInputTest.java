package com.xtra.api.integration;

import com.xtra.api.model.StreamInput;
import com.xtra.api.repository.StreamInputRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class StreamInputTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    StreamInputRepository streamInputRepository;

    @Test
    public void changeDnsTest () throws Exception{
        StreamInput streamInput1 = new StreamInput();
        streamInput1.setUrl("http://before.com");
        var savedStreamInput = streamInputRepository.save(streamInput1);

        StreamInput streamInput2 = new StreamInput();
        streamInput2.setUrl("http://before.com");
        var savedStreamInput2 = streamInputRepository.save(streamInput2);

        StreamInput streamInput3 = new StreamInput();
        streamInput3.setUrl("http://not-before.com");
        var savedStreamInput3 = streamInputRepository.save(streamInput3);


        //Now we are gonna create a streamInputPair via json objects
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("old_dns","http://before.com");
        jsonObject.put("new_dns","http://after.com");

        this.mockMvc.perform(patch("/channels/tools/dns").content(jsonObject.toString()).contentType("application/json")).andDo(print());

    }
}