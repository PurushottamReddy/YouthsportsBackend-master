package com.example.controller;

import com.example.model.UserLogin;
import com.example.service.JwtService;
import com.example.service.UserService;
import com.example.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/auth")
public class LoginController {
    // Logger for logging messages
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Registers a new user.
     *
     * @param newUser The user details to be registered.
     * @return A ResponseEntity containing the signup status.
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody UserLogin newUser) {
        logger.info("Signing up user: {}", newUser);
        try {
            UserLogin createdUser = userService.signupUser(newUser);
            return ResponseEntity.ok(new ApiResponse("User signed up successfully", true));
        } catch (MessagingException | IllegalStateException e) {
            // Log error while signing up user
            logger.error("Error signing up user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param user The user credentials.
     * @return A ResponseEntity containing the authentication status and a JWT token if successful.
     */
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse> signin(@RequestBody UserLogin user) {
        logger.info("Authenticating user: {} with password ,{} ", user.getUserEmail(),user.getPassword());
        // Authenticate user
        ResponseEntity<ApiResponse> res = userService.authenticateUser(user);
        return res;
    }

    /**
     * Verifies the user's email using the provided token.
     *
     * @param token The email verification token.
     * @return A ResponseEntity containing the verification status.
     */
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String token) {
        logger.info("Verifying email with token: {}", token);
        // Verify email using token
        return userService.verifyEmail(token);
    }

    /**
     * Sends a password reset OTP to the user's email.
     *
     * @param userEmail The email of the user requesting the password reset.
     * @return A ResponseEntity containing the password reset status.
     */
    @PostMapping("/request-reset-password")
    public ResponseEntity<ApiResponse> requestResetPassword(@RequestParam String userEmail) {
        logger.info("Requesting password reset for user: {}", userEmail);
        // Request password reset
        return userService.requestPasswordReset(userEmail);
    }

    /**
     * Resets the user's password using the provided OTP and new password.
     *
     * @param userEmail    The email of the user requesting the password reset.
     * @param otp          The password reset OTP.
     * @param newPassword  The new password.
     * @return A ResponseEntity containing the password reset status.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String userEmail, @RequestParam String otp, @RequestParam String newPassword) {
        logger.info("Resetting password for user: {}", userEmail);
        // Reset user password
        return userService.resetPassword(userEmail, otp, newPassword);
    }
}
