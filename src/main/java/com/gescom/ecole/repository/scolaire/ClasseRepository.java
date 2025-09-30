package com.gescom.ecole.repository.scolaire;

import com.gescom.ecole.entity.scolaire.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long>, JpaSpecificationExecutor<Classe> {
    
    Optional<Classe> findByCodeAndAnneeScolaireId(String code, Long anneeScolaireId);
    
    List<Classe> findByNiveauId(Long niveauId);
    
    List<Classe> findByAnneeScolaireId(Long anneeScolaireId);
    
    List<Classe> findByEnseignantPrincipalId(Long enseignantId);
    
    @Query("SELECT c FROM Classe c WHERE c.active = true AND c.anneeScolaire.active = true")
    List<Classe> findActiveClasses();
    
    @Query("SELECT c FROM Classe c WHERE c.effectifActuel < c.capaciteMax")
    List<Classe> findClassesWithAvailableSpace();
    
    @Query("SELECT COUNT(c) FROM Classe c WHERE c.anneeScolaire.id = :anneeScolaireId")
    long countByAnneeScolaireId(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT SUM(c.effectifActuel) FROM Classe c WHERE c.anneeScolaire.id = :anneeScolaireId")
    Integer getTotalEffectifByAnneeScolaire(@Param("anneeScolaireId") Long anneeScolaireId);
}
