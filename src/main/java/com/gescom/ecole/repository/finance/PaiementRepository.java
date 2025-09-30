package com.gescom.ecole.repository.finance;

import com.gescom.ecole.common.enums.ModePaiement;
import com.gescom.ecole.common.enums.StatutPaiement;
import com.gescom.ecole.entity.finance.Paiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long>, JpaSpecificationExecutor<Paiement> {
    
    // Méthodes pour les statistiques
    @Query("SELECT SUM(p.montant) FROM Paiement p")
    BigDecimal sumMontantTotal();
    
    @Query("SELECT SUM(p.montant) FROM Paiement p JOIN p.facture f WHERE f.anneeScolaire.id = :anneeScolaireId")
    BigDecimal sumMontantByAnneeScolaireId(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.datePaiement BETWEEN :debut AND :fin")
    BigDecimal sumMontantByPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
    
    Optional<Paiement> findByNumeroPaiement(String numeroPaiement);
    
    boolean existsByNumeroPaiement(String numeroPaiement);
    
    List<Paiement> findByFactureId(Long factureId);
    
    List<Paiement> findByParentId(Long parentId);
    
    List<Paiement> findByStatut(StatutPaiement statut);
    
    List<Paiement> findByModePaiement(ModePaiement modePaiement);
    
    @Query("SELECT p FROM Paiement p WHERE p.datePaiement BETWEEN :dateDebut AND :dateFin")
    List<Paiement> findByPeriode(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);
    
    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.statut = 'VALIDE' AND p.datePaiement BETWEEN :dateDebut AND :dateFin")
    BigDecimal getTotalPaiementsByPeriode(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);
    
    @Query("SELECT p FROM Paiement p WHERE p.facture.eleve.id = :eleveId")
    List<Paiement> findByEleveId(@Param("eleveId") Long eleveId);
    
    @Query("SELECT p FROM Paiement p WHERE p.facture.anneeScolaire.id = :anneeScolaireId")
    List<Paiement> findByAnneeScolaireId(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.modePaiement = :modePaiement AND p.statut = 'VALIDE'")
    long countByModePaiementAndValide(@Param("modePaiement") ModePaiement modePaiement);
    
    Page<Paiement> findByParentIdOrderByDatePaiementDesc(Long parentId, Pageable pageable);
    
    @Query("SELECT p FROM Paiement p WHERE p.numeroTransaction = :numeroTransaction")
    Optional<Paiement> findByNumeroTransaction(@Param("numeroTransaction") String numeroTransaction);
}
