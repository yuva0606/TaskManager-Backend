package com.yuva.TaskManager.config;

import com.yuva.TaskManager.model.User;
import com.yuva.TaskManager.service.JwtService;
import com.yuva.TaskManager.service.LoginCodeService;
import com.yuva.TaskManager.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class GoogleOAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;
    private final LoginCodeService loginCodeService;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    public GoogleOAuth2SuccessHandler(
            UserService userService,
            JwtService jwtService,
            LoginCodeService loginCodeService) {

        this.userService = userService;
        this.jwtService = jwtService;
        this.loginCodeService = loginCodeService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        OidcUser googleUser =
                (OidcUser) authentication.getPrincipal();

        String googleSubject = googleUser.getSubject();
        String email = googleUser.getEmail();

        // Only accept a verified Google email.
        if (!Boolean.TRUE.equals(googleUser.getEmailVerified())
                || email == null
                || email.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Google email is not verified"
            );

            return;
        }

        System.out.println("Google subject: " + googleSubject);
        System.out.println("Google email: " + email);

        User user;

        try {
            user = userService.findOrCreateGoogleUser(
                    googleSubject,
                    email
            );

        } catch (IllegalStateException e) {

            // An existing password account requires explicit linking.
            response.sendRedirect(
                    redirectUri + "?error=account_link_required"
            );

            return;
        }

        // Generate the TaskManager access token.
        String accessToken =
                jwtService.generateToken(user.getUsername());

        // Generate the refresh token.
        String refreshToken =
                jwtService.generateRefreshToken();

        String refreshTokenHash =
                jwtService.hashRefreshToken(refreshToken);

        Date refreshTokenExpiry =
                new Date(
                        System.currentTimeMillis()
                                + Duration.ofDays(7).toMillis()
                );

        // Store only the refresh-token hash in the database.
        userService.saveRefreshToken(
                user.getUsername(),
                refreshTokenHash,
                refreshTokenExpiry
        );

        // Send the actual refresh token as an HttpOnly cookie.
        ResponseCookie refreshCookie =
                ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .secure(secureCookie)
                        .path("/")
                        .maxAge(Duration.ofDays(7))
                        .sameSite("Lax")
                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshCookie.toString()
        );

        // Create a short-lived code for retrieving the access token.
        String code = loginCodeService.createCode(accessToken);

        String encodedCode = URLEncoder.encode(
                code,
                StandardCharsets.UTF_8
        );

        response.sendRedirect(
                redirectUri + "?code=" + encodedCode
        );
    }
}