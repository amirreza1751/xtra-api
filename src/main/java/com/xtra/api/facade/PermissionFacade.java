package com.xtra.api.facade;

import com.xtra.api.model.Permission;
import com.xtra.api.projection.PermissionDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PermissionFacade {
    private final ModelMapper modelMapper;

    public PermissionFacade() {
        this.modelMapper = new ModelMapper();
    }

    public PermissionDTO convertToDTO(Permission permission) {
        return modelMapper.map(permission, PermissionDTO.class);
    }
}
