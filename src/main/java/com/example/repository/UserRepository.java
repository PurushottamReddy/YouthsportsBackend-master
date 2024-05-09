package com.example.repository;

import com.example.model.UserLogin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserLogin, Long> {
    Optional<UserLogin> findByUserEmail(String userEmail);

	Optional<UserLogin> findByPasswordResetToken(String token);

	Optional<UserLogin> findByUserEmailAndPasswordResetToken(String userEmail, String token);

	boolean existsByUserEmail(String userEmail);

	Optional<UserLogin> findByEmailVerificationToken(String token);
	
}
