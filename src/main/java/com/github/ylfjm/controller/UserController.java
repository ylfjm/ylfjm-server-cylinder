package com.github.ylfjm.controller;

import com.github.ylfjm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

}
