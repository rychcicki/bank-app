package com.example.bank.userRegister;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @PostMapping("/users")
    void registerUser(@RequestBody UserRequest userRequest) {
        userService.registerUser(userRequest);
    }

    @GetMapping("/users/{id}")
    User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping("/users")
    User updateUser(@RequestBody UserRequest userRequest, Long id) {
        return userService.updateUser(userRequest, id);
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
