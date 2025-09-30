package com.gescom.ecole.service.impl;

import com.gescom.ecole.config.security.JwtTokenProvider;
import com.gescom.ecole.dto.auth.JwtResponse;
import com.gescom.ecole.dto.auth.LoginRequest;
import com.gescom.ecole.dto.utilisateur.UtilisateurDTO;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import com.gescom.ecole.mapper.UtilisateurMapper;
import com.gescom.ecole.repository.utilisateur.UtilisateurRepository;
import com.gescom.ecole.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${security.jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        log.info("Tentative de connexion pour l'utilisateur: {}", loginRequest.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = tokenProvider.generateToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);
        
        // Mettre à jour la dernière connexion
        Utilisateur utilisateur = utilisateurRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);
        
        UtilisateurDTO userDTO = utilisateurMapper.toDTO(utilisateur);
        userDTO.setRoles(utilisateur.getRoles().stream()
            .map(role -> role.getCode())
            .collect(Collectors.toSet()));
        userDTO.setPermissions(utilisateur.getAuthorities().stream()
            .map(auth -> auth.getAuthority())
            .collect(Collectors.toSet()));
        
        log.info("Connexion réussie pour l'utilisateur: {}", loginRequest.getUsername());
        
        return JwtResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtExpiration)
            .user(userDTO)
            .build();
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        if (tokenProvider.validateToken(refreshToken)) {
            String username = tokenProvider.extractUsername(refreshToken);
            Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            String newAccessToken = tokenProvider.generateToken(utilisateur);
            String newRefreshToken = tokenProvider.generateRefreshToken(utilisateur);
            
            UtilisateurDTO userDTO = utilisateurMapper.toDTO(utilisateur);
            
            return JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .user(userDTO)
                .build();
        }
        throw new RuntimeException("Token de rafraîchissement invalide");
    }

    @Override
    public void logout(String token) {
        // Invalider le token si nécessaire (blacklist, cache, etc.)
        SecurityContextHolder.clearContext();
        log.info("Déconnexion effectuée");
    }

    @Override
    public void resetPassword(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé avec cet email"));
        
        String resetToken = UUID.randomUUID().toString();
        utilisateur.setResetPasswordToken(resetToken);
        utilisateur.setTokenExpiration(LocalDateTime.now().plusHours(24));
        utilisateurRepository.save(utilisateur);
        
        // TODO: Envoyer l'email avec le token
        log.info("Token de réinitialisation généré pour: {}", email);
    }

    @Override
    public void changePassword(String token, String newPassword) {
        Utilisateur utilisateur = utilisateurRepository.findByResetPasswordToken(token)
            .orElseThrow(() -> new RuntimeException("Token invalide"));
        
        if (utilisateur.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expiré");
        }
        
        utilisateur.setPassword(passwordEncoder.encode(newPassword));
        utilisateur.setResetPasswordToken(null);
        utilisateur.setTokenExpiration(null);
        utilisateurRepository.save(utilisateur);
        
        log.info("Mot de passe changé avec succès pour: {}", utilisateur.getUsername());
    }
}
