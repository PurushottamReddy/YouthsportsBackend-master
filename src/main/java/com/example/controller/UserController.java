package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.UserLogin;
import com.example.service.JwtService;
import com.example.service.UserService;
import com.example.util.ApiResponse;

@RestController
@RequestMapping("/api/userinfo")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Retrieves the user details based on the JWT token provided in the Authorization header.
     * @param headers The HTTP headers containing the Authorization header.
     * @return A ResponseEntity containing the user details or a "User not found" response.
     */
    @GetMapping("/getuserdetails")
    public ResponseEntity<ApiResponse> getUserDetails(@RequestHeader HttpHeaders headers) {
        logger.info("Retrieving user details");
        String userEmail = userService.getUserEmailFromToken(headers.getFirst("Authorization"));
        return userService.getUserDetailsByEmail(userEmail);
    }

    /**
     * Updates the user details based on the provided request body and the user email extracted from the JWT token.
     * @param user The updated user details.
     * @param headers The HTTP headers containing the Authorization header.
     * @return A ResponseEntity containing the update status.
     */
    @PutMapping("/updateuserdetails")
    public ResponseEntity<ApiResponse> updateUserDetails(@RequestBody UserLogin user, @RequestHeader HttpHeaders headers) {
        logger.info("Updating user details");
        String userEmail = userService.getUserEmailFromToken(headers.getFirst("Authorization"));
        user.setUserEmail(userEmail);
        return userService.updateUserDetails(user);
    }
}