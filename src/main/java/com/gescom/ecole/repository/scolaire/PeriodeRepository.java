package com.gescom.ecole.repository.scolaire;

import com.gescom.ecole.common.enums.TypePeriode;
import com.gescom.ecole.entity.scolaire.Periode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface PeriodeRepository extends JpaRepository<Periode, Long> {
    
    Optional<Periode> findByCodeAndAnneeScolaireId(String code, Long anneeScolaireId);
    
    List<Periode> findByAnneeScolaireId(Long anneeScolaireId);
    
    List<Periode> findByAnneeScolaireIdOrderByNumero(Long anneeScolaireId);
    
    List<Periode> findByTypePeriode(TypePeriode typePeriode);
    
    Optional<Periode> findByActiveTrue();
    
    @Query("SELECT p FROM Periode p WHERE p.anneeScolaire.id = :anneeScolaireId AND p.active = true")
    Optional<Periode> findActivePeriodeByAnneeScolaire(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT p FROM Periode p WHERE :date BETWEEN p.dateDebut AND p.dateFin")
    Optional<Periode> findByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(p) FROM Periode p WHERE p.anneeScolaire.id = :anneeScolaireId")
    long countByAnneeScolaireId(@Param("anneeScolaireId") Long anneeScolaireId);
}
