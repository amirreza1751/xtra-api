package com.xtra.api.integration;

import com.xtra.api.controller.admin.CollectionController;
import com.xtra.api.model.Collection;
import com.xtra.api.repository.CollectionRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class CollectionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectionController controller;

    @Autowired
    CollectionRepository repository;

    @Test
    public void getCollections() throws Exception {
        this.mockMvc.perform(get("/collections")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("content")))
                .andExpect(content().contentType("application/json"))
        ;
    }

    @Test
    public void insertCollections() throws Exception {

        JSONArray channels = new JSONArray();
        channels.put(1);

        JSONObject collection = new JSONObject();
        collection.put("name", "collectionTest");
        collection.put("category_name", "test");
        collection.put("type", "CHANNEL");
        collection.put("channels", channels);

        this.mockMvc.perform(post("/collections").content(collection.toString()).contentType("application/json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
        ;
    }

    @Test
    public void updateCollection() throws Exception {
        Collection collection = new Collection();
        collection.setName("test1");
        var saveCollection = repository.save(collection);
        Long id = saveCollection.getId();

        saveCollection.setName("test2");
        JSONObject jo = new JSONObject(saveCollection);


        this.mockMvc.perform(patch("/collections/" + id).content(jo.toString()).contentType("application/json")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(containsString("test2")))
        ;
    }

    @Test
    public void deleteCollection() throws Exception {
        Collection collection = new Collection();
        collection.setName("test1");
        var saveCollection = repository.save(collection);
        Long id = saveCollection.getId();

        this.mockMvc.perform(delete("/collections/" + id).contentType("application/json")).andDo(print())
                .andExpect(status().is2xxSuccessful())
        ;
        assertThat(repository.findById(id).isEmpty());

    }
}
