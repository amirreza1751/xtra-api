package com.xtra.api.mapper.reseller;

import com.xtra.api.model.user.CreditLog;
import com.xtra.api.projection.reseller.subreseller.CreditChangeRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CreditLogMapper {

    public abstract CreditLog toEntity(CreditChangeRequest creditChangeRequest);

}
