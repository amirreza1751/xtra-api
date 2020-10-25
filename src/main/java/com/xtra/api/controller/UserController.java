package com.xtra.api.controller;

import com.xtra.api.model.Line;
import com.xtra.api.model.User;
import com.xtra.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<Page<User>> getLines(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                               @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(userService.findAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @PostMapping("")
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {
        return ResponseEntity.ok(userService.insert(user));
    }
}
