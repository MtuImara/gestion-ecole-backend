package com.gescom.ecole.repository.scolaire;

import com.gescom.ecole.entity.scolaire.AnneeScolaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AnneeScolaireRepository extends JpaRepository<AnneeScolaire, Long>, JpaSpecificationExecutor<AnneeScolaire> {
    
    Optional<AnneeScolaire> findByCode(String code);
    
    Optional<AnneeScolaire> findByActiveTrue();
    
    boolean existsByCode(String code);
    
    List<AnneeScolaire> findByClotureeFalse();
    
    @Query("SELECT a FROM AnneeScolaire a WHERE a.active = true AND a.cloturee = false")
    Optional<AnneeScolaire> findCurrentAnneeScolaire();
    
    @Query("SELECT COUNT(a) FROM AnneeScolaire a WHERE a.cloturee = false")
    long countNonCloturees();
}
