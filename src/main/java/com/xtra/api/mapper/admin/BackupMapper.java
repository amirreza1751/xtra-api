package com.xtra.api.mapper.admin;

import com.xtra.api.model.Backup;
import com.xtra.api.projection.BackupView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.xtra.api.util.Utilities.toReadableFileSize;

@Mapper(componentModel = "spring")
public abstract class BackupMapper {
    @Mapping(source = "fileSize", target = "fileSize", qualifiedByName = "toReadableForm")
    @Mapping(source = "date", target = "date", dateFormat = "yyyy-MM-dd hh:mm:ss")
    public abstract BackupView convertToView(Backup backup);

    @Named("toReadableForm")
    String convertToReadableForm(long size) {
        return toReadableFileSize(size);
    }
}
