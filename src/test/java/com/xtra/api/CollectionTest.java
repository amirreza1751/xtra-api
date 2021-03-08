package com.xtra.api;

import com.xtra.api.controller.admin.CollectionController;
import com.xtra.api.mapper.admin.CollectionMapper;
import com.xtra.api.service.admin.CollectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CollectionController.class)
public class CollectionTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectionMapper collectionMapper;

    @MockBean
    private CollectionService collectionService;

    @Test
    void testGetCollections() throws Exception {
        mockMvc.perform(get("/collections")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }
}
