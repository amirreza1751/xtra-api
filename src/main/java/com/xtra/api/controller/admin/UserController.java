package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.connection.BlockIpRequest;
import com.xtra.api.projection.admin.user.UserView;
import com.xtra.api.service.admin.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
    public ResponseEntity<?> verifyUserByToken(@AuthenticationPrincipal Authentication authentication) {
        if (authentication != null)
            return ResponseEntity.ok(userService.verifyUser(authentication));
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/block-ip")
    public ResponseEntity<?> blockIpAddress(@RequestParam Long id) {
        userService.blockIp(id);
        return ResponseEntity.ok().build();
    }
}
