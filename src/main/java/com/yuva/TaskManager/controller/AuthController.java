package com.yuva.TaskManager.controller;

import com.yuva.TaskManager.Dto.LoginRequest;
import com.yuva.TaskManager.Dto.LoginResponse;
import com.yuva.TaskManager.Dto.TokenResponse;
import com.yuva.TaskManager.model.User;
import com.yuva.TaskManager.service.JwtService;
import com.yuva.TaskManager.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Date;

@RestController
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        String username = authentication.getName();

        String accessToken =
                jwtService.generateToken(username);

        String refreshToken =
                jwtService.generateRefreshToken();

        Date refreshTokenExpiry =
                new Date(
                        System.currentTimeMillis()
                                + 1000L * 60 * 60 * 24 * 7
                );

        String refreshTokenHash =
                jwtService.hashRefreshToken(refreshToken);

        userService.saveRefreshToken(
                username,
                refreshTokenHash,
                refreshTokenExpiry
        );

        ResponseCookie refreshCookie =
                ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(Duration.ofDays(7))
                        .sameSite("Strict")
                        .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshCookie.toString()
                )
                .body(new LoginResponse(accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(name = "refreshToken", required = false)
            String refreshToken) {

        System.out.println("Refresh token: " + refreshToken);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user =
                userService.validateRefreshToken(refreshToken);

        String accessToken =
                jwtService.generateToken(user.getUsername());

        String newRefreshToken =
                jwtService.generateRefreshToken();

        Date newRefreshTokenExpiry =
                new Date(
                        System.currentTimeMillis()
                                + 1000L * 60 * 60 * 24 * 7
                );

        userService.saveRefreshToken(
                user.getUsername(),
                jwtService.hashRefreshToken(newRefreshToken),
                newRefreshTokenExpiry
        );

        ResponseCookie refreshCookie =
                ResponseCookie.from("refreshToken", newRefreshToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(Duration.ofDays(7))
                        .sameSite("Strict")
                        .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshCookie.toString()
                )
                .body(new TokenResponse(accessToken));
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false)
            String refreshToken
    ) {

        System.out.println("Logout token: " + refreshToken);
        userService.logout(refreshToken);

        ResponseCookie refreshCookie =
                ResponseCookie.from("refreshToken", "")
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(Duration.ZERO)
                        .sameSite("Strict")
                        .build();

        System.out.println("Logout successful");

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }
}
