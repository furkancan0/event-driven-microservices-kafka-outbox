package com.ecommerce.service;

import com.ecommerce.dto.AuthenticationRequest;
import com.ecommerce.dto.AuthenticationResponse;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.exception.ApiRequestException;
import com.ecommerce.model.Role;
import com.ecommerce.model.RoleEnum;
import com.ecommerce.model.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private  final UserRepository userRepository;
    private  final RoleRepository roleRepository;
    private  final JwtService jwtService;
    private  final PasswordEncoder passwordEncoder;
    private  final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        Role role = roleRepository.findByName(RoleEnum.USER);//New accounts have default user role
        var user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .registrationDate(LocalDateTime.now())
                .role(role)
                .build();
        userRepository.save(user);
        String jwtToken = jwtService.createToken(registerRequest.getEmail(), String.valueOf(role.getName()));
        return AuthenticationResponse.builder().accessToken(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = getUserPrincipalByEmail(request.getEmail());
        String jwtToken = jwtService.createToken(user.getEmail(), String.valueOf(user.getRole().getName()));
        return AuthenticationResponse.builder().accessToken(jwtToken).build();

    }

    public Long getCurrentUserId() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attribs).getRequest();
        return Long.parseLong(request.getHeader("auth-user-id"));
    }

    public User getUserPrincipalByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiRequestException("User not found", HttpStatus.NOT_FOUND));
    }

}