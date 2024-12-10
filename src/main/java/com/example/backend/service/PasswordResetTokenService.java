package com.example.backend.service;

import com.example.backend.model.PasswordResetToken;
import com.example.backend.model.User;
import com.example.backend.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    /**
     * Create a password reset token for a user from a generated token
     *
     * @param user  The user who is resetting their password
     * @param token The randomly generated token
     */
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    /**
     * Validate a password reset token
     *
     * @param token The password reset token to be validated
     * @return The email of the user who is attached to the token
     */
    public String validateToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        if (resetToken.getExpiryDate().before(cal.getTime())) {
            passwordResetTokenRepository.delete(resetToken);
            return null;
        }

        return resetToken.getUser().getEmail();
    }

    /**
     * Clears a password reset token from the database
     *
     * @param token Password reset token to be cleared
     */
    public void clearToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken != null) {
            passwordResetTokenRepository.delete(resetToken);
        }
    }

}
