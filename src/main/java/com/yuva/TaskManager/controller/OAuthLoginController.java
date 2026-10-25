package com.yuva.TaskManager.controller;

import com.yuva.TaskManager.service.LoginCodeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class OAuthLoginController {

    private final LoginCodeService loginCodeService;

    public OAuthLoginController(
            LoginCodeService loginCodeService) {

        this.loginCodeService = loginCodeService;
    }

    public record ExchangeRequest(String code) {}

    @PostMapping("/exchange")
    public ResponseEntity<?> exchangeCode(
            @RequestBody ExchangeRequest request) {

        String accessToken =
                loginCodeService.consumeCode(request.code());

        if (accessToken == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Invalid, expired, or already-used login code"
                    ));
        }

        return ResponseEntity.ok(
                Map.of("accessToken", accessToken)
        );
    }
}