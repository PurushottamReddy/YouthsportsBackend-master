package com.example.service;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.example.model.AccountType;
import com.example.model.UserLogin;
import com.example.repository.UserRepository;
import com.example.service.EmailService;
import com.example.service.UserService;
import com.example.util.ApiResponse;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Date;
import java.util.Optional;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;
    
    @Test
    void testSignupUser_UserAlreadyExists_ThrowsException() {
        UserLogin existingUser = new UserLogin("existing@example.com", "password123", true);
        when(userRepository.existsByUserEmail(existingUser.getUserEmail())).thenReturn(true);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            userService.signupUser(existingUser);
        });

        assertEquals("User with email " + existingUser.getUserEmail() + " already exists.", exception.getMessage());
        verify(userRepository, never()).save(any(UserLogin.class));
    }


    @Test
    void testSignupUser_NewUser_SuccessfulRegistration() throws Exception {
        // Arrange
        UserLogin newUser = new UserLogin("test@example.com", "password123", false);
        when(userRepository.existsByUserEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(UserLogin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserLogin result = userService.signupUser(newUser);

        // Assert
        verify(userRepository, times(2)).save(any(UserLogin.class));
        assertEquals("test@example.com", result.getUserEmail());
        assertNotNull(result.getEmailVerificationToken(), "The email verification token should be set");
    }



    @Test
    void testSignupUser_EmailAlreadyExists_ThrowsException() {
        // Given
        UserLogin newUser = new UserLogin("existing@example.com", "password123", false);
        when(userRepository.existsByUserEmail(newUser.getUserEmail())).thenReturn(true);

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            userService.signupUser(newUser);
        });

        assertEquals("User with email " + newUser.getUserEmail() + " already exists.", exception.getMessage());
        verify(userRepository, never()).save(any(UserLogin.class));
    }


    @Test
    void testVerifyEmail_ValidToken_SuccessfulVerification() {
        UserLogin user = new UserLogin("test@example.com", "password123", true);
        user.setEmailVerificationToken("valid-token");
        user.setEmailVerificationTokenExpiry(new Date(System.currentTimeMillis() + 10000));  

        when(userRepository.findByEmailVerificationToken("valid-token")).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse> response = userService.verifyEmail("valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email verified successfully", response.getBody().getMessage());
    }



    @Test
    void testVerifyEmail_InvalidToken_ReturnsError() {
        // Given
        when(userRepository.findByEmailVerificationToken("invalid-token")).thenReturn(Optional.empty());

        // When
        ResponseEntity<ApiResponse> response = userService.verifyEmail("invalid-token");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Verification link is invalid or expired", response.getBody().getMessage());
        verify(userRepository, never()).save(any(UserLogin.class));
    }
    
    @Test
    void testAuthenticateUser_ValidCredentials_SuccessfulAuthentication() {
        UserLogin storedUser = new UserLogin("test@example.com", "password123", true);
        storedUser.setAccountType(AccountType.Player);  
        storedUser.setIsUserVerified(true);

        when(userRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(storedUser));

        ResponseEntity<ApiResponse> response = userService.authenticateUser(new UserLogin("test@example.com", "password123", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals("User authenticated successfully", response.getBody().getMessage());
    }



}
