package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByResetPasswordToken(String token);

    @Query("SELECT u FROM User u WHERE u.resetPasswordToken = :token AND u.resetPasswordExpiry > :now")
    Optional<User> findValidResetToken(String token, LocalDateTime now);

    @Query("SELECT u FROM User u WHERE u.emailVerificationToken = :token AND u.emailVerificationExpiry > :now")
    Optional<User> findValidVerificationToken(String token, LocalDateTime now);
}
