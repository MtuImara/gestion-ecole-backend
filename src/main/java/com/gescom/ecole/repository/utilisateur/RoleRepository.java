package com.gescom.ecole.repository.utilisateur;

import com.gescom.ecole.entity.utilisateur.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Role> findByActif(Boolean actif);
    
    List<Role> findByDesignationContainingIgnoreCase(String designation);
}
