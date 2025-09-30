package com.gescom.ecole.service;

import com.gescom.ecole.dto.auth.JwtResponse;
import com.gescom.ecole.dto.auth.LoginRequest;

public interface AuthService {
    JwtResponse login(LoginRequest loginRequest);
    JwtResponse refreshToken(String refreshToken);
    void logout(String token);
    void resetPassword(String email);
    void changePassword(String token, String newPassword);
}
