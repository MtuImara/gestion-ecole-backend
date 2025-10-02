package com.gescom.ecole.service;

import com.gescom.ecole.dto.utilisateur.UtilisateurDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserManagementService {
    
    Page<UtilisateurDTO> findAll(Pageable pageable);
    
    UtilisateurDTO findById(Long id);
    
    UtilisateurDTO createUser(UtilisateurDTO userDTO);
    
    UtilisateurDTO updateUser(Long id, UtilisateurDTO userDTO);
    
    void deleteUser(Long id);
    
    UtilisateurDTO activateUser(Long id);
    
    UtilisateurDTO deactivateUser(Long id);
    
    void changePassword(Long id, String newPassword);
    
    UtilisateurDTO changeRole(Long id, String newRole);
    
    Page<UtilisateurDTO> searchUsers(String query, Pageable pageable);
    
    Page<UtilisateurDTO> findByRole(String role, Pageable pageable);
    
    Map<String, Object> getStatistics();
}
