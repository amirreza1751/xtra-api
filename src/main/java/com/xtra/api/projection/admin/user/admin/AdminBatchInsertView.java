package com.xtra.api.projection.admin.user.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.user.Admin;
import com.xtra.api.projection.admin.user.UserInsertView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Validated
public class AdminBatchInsertView {
    protected String isBanned;
    @Nullable
    protected Long roleId;

    Set<Long> ids;
}
