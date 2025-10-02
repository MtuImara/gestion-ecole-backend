package com.gescom.ecole.controller;

import com.gescom.ecole.dto.utilisateur.UtilisateurDTO;
import com.gescom.ecole.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Gestion Utilisateurs", description = "Administration des utilisateurs")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    @Operation(summary = "Liste des utilisateurs", description = "Récupère tous les utilisateurs (Admin uniquement)")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UtilisateurDTO>> getAllUsers(@PageableDefault Pageable pageable) {
        Page<UtilisateurDTO> users = userManagementService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails utilisateur", description = "Récupère un utilisateur par son ID")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UtilisateurDTO> getUserById(@PathVariable Long id) {
        UtilisateurDTO user = userManagementService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "Créer un utilisateur", description = "Crée un nouvel utilisateur")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UtilisateurDTO> createUser(@Valid @RequestBody UtilisateurDTO userDTO) {
        UtilisateurDTO created = userManagementService.createUser(userDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un utilisateur", description = "Modifie un utilisateur existant")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UtilisateurDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurDTO userDTO) {
        UtilisateurDTO updated = userManagementService.updateUser(id, userDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activer un utilisateur", description = "Active un compte utilisateur")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UtilisateurDTO> activateUser(@PathVariable Long id) {
        UtilisateurDTO user = userManagementService.activateUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver un utilisateur", description = "Désactive un compte utilisateur")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UtilisateurDTO> deactivateUser(@PathVariable Long id) {
        UtilisateurDTO user = userManagementService.deactivateUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/change-password")
    @Operation(summary = "Changer le mot de passe", description = "Change le mot de passe d'un utilisateur")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordData) {
        String newPassword = passwordData.get("newPassword");
        userManagementService.changePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/change-role")
    @Operation(summary = "Changer le rôle", description = "Change le rôle d'un utilisateur")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UtilisateurDTO> changeRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> roleData) {
        String newRole = roleData.get("role");
        UtilisateurDTO user = userManagementService.changeRole(id, newRole);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des utilisateurs", description = "Recherche par nom, email ou username")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UtilisateurDTO>> searchUsers(
            @RequestParam String query,
            @PageableDefault Pageable pageable) {
        Page<UtilisateurDTO> users = userManagementService.searchUsers(query, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/by-role/{role}")
    @Operation(summary = "Utilisateurs par rôle", description = "Récupère les utilisateurs par rôle")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UtilisateurDTO>> getUsersByRole(
            @PathVariable String role,
            @PageableDefault Pageable pageable) {
        Page<UtilisateurDTO> users = userManagementService.findByRole(role, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Statistiques utilisateurs", description = "Statistiques des utilisateurs")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = userManagementService.getStatistics();
        return ResponseEntity.ok(stats);
    }
}
