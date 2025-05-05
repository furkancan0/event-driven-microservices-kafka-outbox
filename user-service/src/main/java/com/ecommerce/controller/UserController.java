package com.ecommerce.controller;

import com.ecommerce.dto.UserResponse;
import com.ecommerce.model.User;
import com.ecommerce.service.AuthService;
import com.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user/")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @Transactional(readOnly = true)
    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        User user = userService.getAuthenticatedUser();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{email}")
    public UserResponse getUserPrincipalByEmail(@PathVariable("email") String email) {
        User user = authService.getUserPrincipalByEmail(email);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .build();
    }
}
