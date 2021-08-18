package com.xtra.api.controller.admin;

import com.google.zxing.WriterException;
import com.xtra.api.projection.admin.user.UserView;
import com.xtra.api.service.admin.UserService;
import com.xtra.api.service.system.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserAuthService userAuthService;

    @Autowired
    public UserController(UserService userService, UserAuthService userAuthService) {
        this.userService = userService;
        this.userAuthService = userAuthService;
    }

    @GetMapping("")
    public ResponseEntity<Page<UserView>> getAllUsers(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                   @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(userService.getAllViews(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserView> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getViewById(id));
    }

    //auth
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUserByToken(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null)
            return ResponseEntity.ok(userService.verifyUser(userDetails));
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/block-ip")
    public ResponseEntity<?> blockIpAddress(@RequestParam Long id) {
        userService.blockIp(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/barcode")
    public int barcode() throws IOException, WriterException {
        UserAuthService.createQRCode("otpauth://totp/My%20Awesome%20Company%3Atest%40gmail.com?secret=RT6HTNXMNODHOCS5HKTOW2NFCJWWB75Z&issuer=My%20Awesome%20Company", "/home/amirak/Desktop/barcode.png", 200, 200);
        return 1;
    }
}
