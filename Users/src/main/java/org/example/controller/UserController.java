package org.example.controller;

import org.example.apiresponse.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public User getUser() {
        return new User();
    }
}
