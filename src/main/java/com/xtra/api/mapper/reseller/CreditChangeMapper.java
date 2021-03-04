package com.xtra.api.mapper.reseller;

import com.xtra.api.model.CreditChangeLog;
import com.xtra.api.projection.reseller.subreseller.CreditChangeRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CreditChangeMapper {

    public abstract CreditChangeLog toEntity(CreditChangeRequest creditChangeRequest);

}
