package com.xtra.api.controller;

import com.xtra.api.model.Line;
import com.xtra.api.projection.UserInsertView;
import com.xtra.api.projection.UserView;
import com.xtra.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.xtra.api.security.SecurityConstants.HEADER_STRING;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<Page<UserView>> getLines(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                   @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(userService.getAllViews(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserView> getLine(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getViewById(id));
    }

    @PostMapping("")
    public ResponseEntity<UserView> addUser(@RequestBody UserInsertView insertView) {
        return ResponseEntity.ok(userService.insert(insertView));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserView> updateUser(@PathVariable Long id, @RequestBody UserInsertView view) {
        return ResponseEntity.ok(userService.updateOrFail(id, view));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //auth
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUserByToken(@AuthenticationPrincipal Authentication authentication) {
        if (authentication != null)
            return ResponseEntity.ok(userService.verifyUser(authentication));
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
