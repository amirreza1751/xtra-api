package com.xtra.api;

import com.xtra.api.controller.LineController;
import com.xtra.api.mapper.DownloadListMapper;
import com.xtra.api.model.DownloadList;
import com.xtra.api.model.Line;
import com.xtra.api.projection.DlCollectionDto;
import com.xtra.api.projection.DownloadListDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    private DownloadListMapper listMapper;

    @Test
    public void DlMapperTest() {
        DownloadListDto dto = new DownloadListDto();
        dto.setId(1L);
        var collections = new ArrayList<DlCollectionDto>();
        dto.setCollections(collections);
        var entity = listMapper.convertToEntity(dto);
        System.out.println();
    }

}
