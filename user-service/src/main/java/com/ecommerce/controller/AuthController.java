package com.ecommerce.controller;

import com.ecommerce.dto.AuthenticationRequest;
import com.ecommerce.dto.AuthenticationResponse;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UserResponse;
import com.ecommerce.model.User;
import com.ecommerce.service.AuthService;
import com.ecommerce.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest registerRequest
    ) {
        AuthenticationResponse authResponse = authService.register(registerRequest);
        return  ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<UserResponse> validateToken(@RequestBody String token) {
        String email = jwtService.validateToken(token);
        User user = authService.getUserPrincipalByEmail(email);
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .enabled(user.isEnabled())
                .email(email).build();
        return ResponseEntity.ok(userResponse);
    }
}
