package com.xtra.api.controller;

import com.xtra.api.model.Line;
import com.xtra.api.model.User;
import com.xtra.api.projection.UserInsertView;
import com.xtra.api.projection.UserView;
import com.xtra.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<UserView> addUser(@RequestBody UserInsertView insertView) {
        return ResponseEntity.ok(userService.insert(insertView));
    }
}
