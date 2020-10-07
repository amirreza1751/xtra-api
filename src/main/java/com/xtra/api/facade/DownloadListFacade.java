package com.xtra.api.facade;

import com.xtra.api.model.DownloadList;
import com.xtra.api.projection.DownloadListDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DownloadListFacade {
    private final ModelMapper modelMapper;

    public DownloadListFacade() {
        this.modelMapper = new ModelMapper();
    }

    public DownloadListDto convertToDto(DownloadList downloadList) {
        DownloadListDto dto = modelMapper.map(downloadList, DownloadListDto.class);
        return dto;
    }

    public DownloadList convertToEntity(DownloadListDto downloadListDto) {
        return modelMapper.map(downloadListDto, DownloadList.class);
    }
}
