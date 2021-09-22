package com.xtra.api.projection;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordUpdateView {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
