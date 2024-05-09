package com.example.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.model.UserLogin;
import com.example.repository.UserRepository;
import com.example.util.ApiResponse;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private HttpServletRequest request;
    
    @Autowired
    private JwtService jwtService;

    /**
     * Registers a new user.
     * @param user The user details to be registered.
     * @return The saved user object.
     * @throws MessagingException If there's an info sending the email verification.
     */
    public UserLogin signupUser(UserLogin user) throws MessagingException {
        if (userRepository.existsByUserEmail(user.getUserEmail())) {
            logger.info("User with email {} already exists.", user.getUserEmail());
            throw new IllegalStateException("User with email " + user.getUserEmail() + " already exists.");
        }

        user.setIsUserVerified(false);
        user.setCreatedTimestamp(new Date());
        UserLogin savedUser = userRepository.save(user);
        generateEmailVerificationToken(savedUser);
        return savedUser;
    }

    /**
     * Generates an email verification token and sends a verification email to the user.
     * @param user The user to be verified.
     * @throws MessagingException If there's an info sending the email.
     */
    private void generateEmailVerificationToken(UserLogin user) throws MessagingException {
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiry(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        userRepository.save(user);

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String verificationUrl = baseUrl + "/auth/verify-email?token=" + token;
        emailService.sendEmail(user.getUserEmail(), "Verify Your Email", "Please click on the link to verify your email: " + verificationUrl);
        logger.info("Email verification token generated and email sent to user: {}", user.getUserEmail());
    }

    /**
     * Authenticates a user with the provided credentials.
     * @param user The user credentials.
     * @return A ResponseEntity containing the authentication status and a JWT token if successful.
     */
    public ResponseEntity<ApiResponse> authenticateUser(UserLogin user) {
        Optional<UserLogin> foundUser = userRepository.findByUserEmail(user.getUserEmail());
        HttpHeaders responseHeaders = new HttpHeaders();

        return foundUser.map(u -> {
            if (!u.getIsUserVerified()) {
                logger.info("User {} is not verified.", u.getUserEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .headers(responseHeaders)
                        .body(new ApiResponse("Email not verified", false));
            }
            if (u.getPassword().equals(user.getPassword())) {
                logger.info("User {} authenticated successfully.", u.getUserEmail());
                u.setLastLoginTimestamp(new Date());
                userRepository.save(u);

                String jwtToken = jwtService.generateToken(u.getUserEmail(), u.getAccountType().toString());
                responseHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);

                // Log the JWT token
                logger.info("Generated JWT Token for user {}: {}", u.getUserEmail(), jwtToken);

                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body(new ApiResponse("User authenticated successfully", true));
            } else {
                logger.info("User {} provided invalid credentials.", user.getUserEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .headers(responseHeaders)
                        .body(new ApiResponse("Invalid credentials", false));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Verifies the user's email using the provided token.
     * @param token The email verification token.
     * @return A ResponseEntity containing the verification status.
     */
    public ResponseEntity<ApiResponse> verifyEmail(String token) {
        Optional<UserLogin> userOptional = userRepository.findByEmailVerificationToken(token)
            .filter(user -> new Date().before(user.getEmailVerificationTokenExpiry()));

        if (userOptional.isPresent()) {
            UserLogin user = userOptional.get();
            user.setIsUserVerified(true);
            user.setEmailVerificationToken(null);
            userRepository.save(user);
            logger.info("User {} verified email successfully.", user.getUserEmail());
            return ResponseEntity.ok(new ApiResponse("Email verified successfully", true));
        }
        logger.warn("Invalid or expired email verification token provided.");
        return ResponseEntity.badRequest().body(new ApiResponse("Verification link is invalid or expired", false));
    }

    /**
     * Sends a password reset OTP to the user's email.
     * @param userEmail The email of the user requesting the password reset.
     * @return A ResponseEntity containing the password reset status.
     */
    public ResponseEntity<ApiResponse> requestPasswordReset(String userEmail) {
        Optional<UserLogin> userOptional = userRepository.findByUserEmail(userEmail);
        if (!userOptional.isPresent()) {
            logger.warn("User with email {} not found.", userEmail);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User email not found!", false));
        }

        try {
            UserLogin user = userOptional.get();
            String otp = generateOtp(); 
            user.setPasswordResetToken(otp);
            user.setPasswordResetTokenExpiry(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)); // 1 week expiry            
            userRepository.save(user);

            emailService.sendEmail(userEmail, "Reset Your Password", "Your OTP for password reset is: " + otp);
            logger.info("Password reset OTP sent to user: {}", userEmail);

            return ResponseEntity.ok(new ApiResponse("Reset password OTP sent successfully.", true));
        } catch (MessagingException e) {
            logger.info("Failed to send password reset OTP to user: {}", userEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to send reset password OTP.", false));
        }
    }
    
    /**
     * Generates a 6-digit OTP.
     * @return The generated OTP as a string.
     */
    private String generateOtp() {
        int otp = 100000 + new Random().nextInt(900000); // Generate 6 digit OTP
        return String.valueOf(otp);
    }

    /**
     * Resets the user's password using the provided OTP and new password.
     * @param userEmail The email of the user requesting the password reset.
     * @param otp The password reset OTP.
     * @param newPassword The new password.
     * @return A ResponseEntity containing the password reset status.
     */
    public ResponseEntity<ApiResponse> resetPassword(String userEmail, String otp, String newPassword) {
        Optional<UserLogin> userOptional = userRepository.findByUserEmailAndPasswordResetToken(userEmail, otp)
                .filter(user -> new Date().before(user.getPasswordResetTokenExpiry()));

        if (!userOptional.isPresent()) {
            logger.warn("Invalid OTP or expired password reset token provided for user: {}", userEmail);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Invalid OTP or expired.", false));
        }
        UserLogin user = userOptional.get();
        user.setPassword(newPassword); // Consider encrypting the password before saving
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        logger.info("Password reset successfully for user: {}", userEmail);

        return ResponseEntity.ok(new ApiResponse("Password reset successfully", true));
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        UserLogin userLogin = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userLogin.getAccountType().toString()));
        
        return new User(
                userLogin.getUserEmail(),
                userLogin.getPassword(),
                authorities);
    }

    /**
     * Retrieves the user details by email.
     * @param userEmail The email of the user.
     * @return A ResponseEntity containing the user details or a "User not found" response.
     */
    public ResponseEntity getUserDetailsByEmail(String userEmail) {
        logger.info("Retrieving user details for email: {}", userEmail);
        UserLogin user = userRepository.findByUserEmail(userEmail).orElse(null);
        if (user != null) {
            logger.info("User details retrieved successfully for email: {}", userEmail);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            logger.warn("User not found for email: {}", userEmail);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User not found", false));
        }
    }

    /**
     * Updates the user details.
     * @param user The updated user details.
     * @return A ResponseEntity containing the update status.
     */
    public ResponseEntity<ApiResponse> updateUserDetails(UserLogin user) {
        // Retrieve existing user details from the database
        Optional<UserLogin> userOptional = userRepository.findByUserEmail(user.getUserEmail());
        if (userOptional.isPresent()) {
            // Update the relevant fields of the retrieved UserLogin object
            UserLogin existingUser = userOptional.get();
            existingUser.setContactNumber(user.getContactNumber());
            existingUser.setName(user.getName());
             
            // Save the updated UserLogin object back to the database
            userRepository.save(existingUser);
            logger.info("User details updated successfully for email: {}", user.getUserEmail());
            return ResponseEntity.ok(new ApiResponse("User details updated successfully", true));
        } else {
            logger.warn("User not found for email: {}", user.getUserEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User not found", false));
        }
    }
    
    /**
     * Extracts the user email from the provided JWT token.
     * @param jwtToken The JWT token.
     * @return The user email extracted from the token.
     */
    public String getUserEmailFromToken(String jwtToken) {
    	String token = jwtToken.substring(7).trim();
        String userEmail = jwtService.extractUsername(token);
        logger.info("User email extracted from JWT token: {}", userEmail);
        return userEmail;
    }
    
    /**
     * Retrieves the account type of the user based on their email.
     * @param userEmail The email of the user.
     * @return The account type of the user.
     */
    public String getAccountTypeBasedOnEmail(String userEmail) {
    	logger.info("Retrieving account type for user email: {}", userEmail);
    	return userRepository.findByUserEmail(userEmail).get().getAccountType().toString();
    }
}