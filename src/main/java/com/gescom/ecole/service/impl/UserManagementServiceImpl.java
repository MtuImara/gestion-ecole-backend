package com.gescom.ecole.service.impl;

import com.gescom.ecole.dto.utilisateur.UtilisateurDTO;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import com.gescom.ecole.exception.BusinessException;
import com.gescom.ecole.exception.ResourceNotFoundException;
import com.gescom.ecole.repository.utilisateur.UtilisateurRepository;
import com.gescom.ecole.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserManagementServiceImpl implements UserManagementService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UtilisateurDTO> findAll(Pageable pageable) {
        Page<Utilisateur> users = utilisateurRepository.findAll(pageable);
        return users.map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurDTO findById(Long id) {
        Utilisateur user = utilisateurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return toDTO(user);
    }

    @Override
    public UtilisateurDTO createUser(UtilisateurDTO userDTO) {
        log.info("Création utilisateur: {}", userDTO.getUsername());
        
        if (utilisateurRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new BusinessException("Ce nom d'utilisateur existe déjà");
        }
        
        if (utilisateurRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new BusinessException("Cet email est déjà utilisé");
        }
        
        Utilisateur user = new Utilisateur();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setTelephone(userDTO.getTelephone());
        user.setActif(true);
        user.setCompteVerrouille(false);
        
        user = utilisateurRepository.save(user);
        log.info("Utilisateur créé: ID={}", user.getId());
        
        return toDTO(user);
    }

    @Override
    public UtilisateurDTO updateUser(Long id, UtilisateurDTO userDTO) {
        log.info("Mise à jour utilisateur ID: {}", id);
        
        Utilisateur user = utilisateurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        if (!user.getEmail().equals(userDTO.getEmail())) {
            if (utilisateurRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new BusinessException("Cet email est déjà utilisé");
            }
            user.setEmail(userDTO.getEmail());
        }
        
        user.setTelephone(userDTO.getTelephone());
        user.setTelephoneSecondaire(userDTO.getTelephoneSecondaire());
        
        user = utilisateurRepository.save(user);
        return toDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Suppression utilisateur ID: {}", id);
        
        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé");
        }
        
        utilisateurRepository.deleteById(id);
    }

    @Override
    public UtilisateurDTO activateUser(Long id) {
        Utilisateur user = utilisateurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        user.setActif(true);
        user.setCompteVerrouille(false);
        user = utilisateurRepository.save(user);
        
        log.info("Utilisateur activé: {}", user.getUsername());
        return toDTO(user);
    }

    @Override
    public UtilisateurDTO deactivateUser(Long id) {
        Utilisateur user = utilisateurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        user.setActif(false);
        user = utilisateurRepository.save(user);
        
        log.info("Utilisateur désactivé: {}", user.getUsername());
        return toDTO(user);
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        Utilisateur user = utilisateurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        utilisateurRepository.save(user);
        
        log.info("Mot de passe changé pour: {}", user.getUsername());
    }

    @Override
    public UtilisateurDTO changeRole(Long id, String newRole) {
        Utilisateur user = utilisateurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        // TODO: Implémenter changement de rôle selon votre modèle
        log.info("Rôle changé pour: {} -> {}", user.getUsername(), newRole);
        
        return toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UtilisateurDTO> searchUsers(String query, Pageable pageable) {
        // TODO: Implémenter recherche - utiliser findAll pour l'instant
        Page<Utilisateur> users = utilisateurRepository.findAll(pageable);
        return users.map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UtilisateurDTO> findByRole(String role, Pageable pageable) {
        // TODO: Implémenter selon votre modèle de rôles
        return Page.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        long total = utilisateurRepository.count();
        stats.put("totalUsers", total);
        stats.put("activeUsers", 0); // TODO: implémenter comptage
        stats.put("inactiveUsers", 0); // TODO: implémenter comptage
        
        return stats;
    }
    
    private UtilisateurDTO toDTO(Utilisateur user) {
        return UtilisateurDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .telephone(user.getTelephone())
            .telephoneSecondaire(user.getTelephoneSecondaire())
            .photoUrl(user.getPhotoUrl())
            .derniereConnexion(user.getDerniereConnexion())
            .actif(user.getActif())
            .compteVerrouille(user.getCompteVerrouille())
            .type(user.getClass().getSimpleName())
            .build();
    }
}
