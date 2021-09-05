package com.xtra.api.controller.admin;


import com.google.zxing.WriterException;
import com.xtra.api.service.system.UserAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;


@RestController
@RequestMapping("/user/profile")
public class ProfileController {
    private final UserAuthService userAuthService;

    public ProfileController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    //2fa routes
    @GetMapping(value = "/2fa/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> getQRCode() throws WriterException {
        return ResponseEntity.ok()
                .body(userAuthService.getQRCode());
    }

    @GetMapping(value = "/2fa/enable")
    public ResponseEntity<?> enable2FA(@RequestParam long totp) {
        return ResponseEntity.status(userAuthService.enable2FA(totp)).build();
    }

    @GetMapping(value = "/2fa/disable")
    public ResponseEntity<?> disable2FA() {
        userAuthService.disable2FA();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/2fa/status")
    public ResponseEntity<Boolean> check2FAStatus() {
        return ResponseEntity.ok(userAuthService.check2FAStatus());
    }
}
