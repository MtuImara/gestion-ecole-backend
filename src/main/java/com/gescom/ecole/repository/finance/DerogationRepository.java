package com.gescom.ecole.repository.finance;

import com.gescom.ecole.common.enums.StatutDerogation;
import com.gescom.ecole.entity.finance.Derogation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DerogationRepository extends JpaRepository<Derogation, Long> {
    
    Optional<Derogation> findByNumeroDerogation(String numeroDerogation);
    
    boolean existsByNumeroDerogation(String numeroDerogation);
    
    List<Derogation> findByEleveId(Long eleveId);
    
    List<Derogation> findByParentId(Long parentId);
    
    Page<Derogation> findByStatut(StatutDerogation statut, Pageable pageable);
    
    @Query("SELECT d FROM Derogation d WHERE d.statut = :statut AND d.dateDemande BETWEEN :dateDebut AND :dateFin")
    List<Derogation> findByStatutAndPeriode(
        @Param("statut") StatutDerogation statut,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );
    
    @Query("SELECT COUNT(d) FROM Derogation d WHERE d.statut = :statut")
    Long countByStatut(@Param("statut") StatutDerogation statut);
    
    @Query("SELECT d FROM Derogation d WHERE d.eleve.id = :eleveId AND d.statut = :statut")
    List<Derogation> findByEleveIdAndStatut(@Param("eleveId") Long eleveId, @Param("statut") StatutDerogation statut);
}
