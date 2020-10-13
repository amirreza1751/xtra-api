package com.xtra.api;

import com.xtra.api.mapper.DownloadListMapper;
import com.xtra.api.projection.DlCollectionDto;
import com.xtra.api.projection.DownloadListView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    private DownloadListMapper listMapper;

    @Test
    public void DlMapperTest() {
        DownloadListView dto = new DownloadListView();
        dto.setId(1L);
        var collections = new ArrayList<DlCollectionDto>();
        dto.setCollections(collections);
        var entity = listMapper.convertToEntity(dto);
        System.out.println();
    }

}
