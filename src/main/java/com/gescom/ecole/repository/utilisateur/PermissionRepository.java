package com.gescom.ecole.repository.utilisateur;

import com.gescom.ecole.entity.utilisateur.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Permission> findByModule(String module);
    
    List<Permission> findByAction(String action);
    
    List<Permission> findByModuleAndAction(String module, String action);
}
