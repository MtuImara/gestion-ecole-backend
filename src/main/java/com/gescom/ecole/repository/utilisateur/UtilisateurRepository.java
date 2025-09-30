package com.gescom.ecole.repository.utilisateur;

import com.gescom.ecole.entity.utilisateur.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>, JpaSpecificationExecutor<Utilisateur> {
    
    Optional<Utilisateur> findByUsername(String username);
    Optional<Utilisateur> findByEmail(String email);
    
    // Vérification d'existence
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    // Méthodes pour les statistiques
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE TYPE(u) = :type")
    Long countByType(@Param("type") String type);
    
    Optional<Utilisateur> findByResetPasswordToken(String token);
    
    @Query("SELECT u FROM Utilisateur u JOIN u.roles r WHERE r.code = :roleCode")
    List<Utilisateur> findByRoleCode(@Param("roleCode") String roleCode);
    
    @Query("SELECT u FROM Utilisateur u WHERE u.actif = true")
    List<Utilisateur> findAllActive();
    
    @Query("SELECT u FROM Utilisateur u WHERE u.compteVerrouille = true")
    List<Utilisateur> findAllLocked();
    
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.actif = true")
    long countActiveUsers();
    
    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.telephone) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Utilisateur> searchUsers(@Param("search") String search);
}
