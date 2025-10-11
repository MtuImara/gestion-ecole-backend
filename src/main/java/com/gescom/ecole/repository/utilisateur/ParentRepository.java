package com.gescom.ecole.repository.utilisateur;

import com.gescom.ecole.entity.utilisateur.Parent;
import com.gescom.ecole.common.enums.TypeParent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    
    Optional<Parent> findByEmail(String email);
    
    Optional<Parent> findByCin(String cin);
    
    Optional<Parent> findByNumeroParent(String numeroParent);
    
    boolean existsByEmail(String email);
    
    boolean existsByCin(String cin);
    
    boolean existsByNumeroParent(String numeroParent);
    
    List<Parent> findByTypeParent(TypeParent typeParent);
    
    @Query("SELECT p FROM Parent p WHERE " +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "p.telephone LIKE CONCAT('%', :search, '%') OR " +
           "LOWER(p.profession) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Parent> searchParents(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT p FROM Parent p WHERE " +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "p.telephone LIKE CONCAT('%', :search, '%')")
    List<Parent> searchParentsSimple(@Param("search") String search);
    
    @Query("SELECT p FROM Parent p LEFT JOIN FETCH p.enfants")
    List<Parent> findAllWithEnfants();
    
    @Query("SELECT p FROM Parent p LEFT JOIN FETCH p.enfants WHERE p.id = :id")
    Optional<Parent> findByIdWithEnfants(@Param("id") Long id);
    
    @Query("SELECT COUNT(p) FROM Parent p")
    long countAllParents();
    
    @Query("SELECT p FROM Parent p JOIN p.enfants e WHERE e.id = :eleveId")
    List<Parent> findByEnfantId(@Param("eleveId") Long eleveId);
}
