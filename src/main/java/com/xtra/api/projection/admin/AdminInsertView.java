package com.xtra.api.projection.admin;

import com.xtra.api.projection.user.UserInsertView;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminInsertView extends UserInsertView {
    private String firstName;
    private String lastName;
}
