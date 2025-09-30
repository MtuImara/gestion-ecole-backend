package com.gescom.ecole.repository.finance;

import com.gescom.ecole.common.enums.StatutFacture;
import com.gescom.ecole.common.enums.TypeFacture;
import com.gescom.ecole.entity.finance.Facture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long>, JpaSpecificationExecutor<Facture> {
    
    // Méthodes pour les statistiques
    @Query("SELECT SUM(f.montantTotal) FROM Facture f")
    BigDecimal sumMontantTotal();
    
    @Query("SELECT SUM(f.montantTotal) FROM Facture f WHERE f.anneeScolaire.id = :anneeScolaireId")
    BigDecimal sumMontantTotalByAnneeScolaireId(@Param("anneeScolaireId") Long anneeScolaireId);
    
    Long countByStatut(String statut);
    
    Long countByAnneeScolaireId(Long anneeScolaireId);
    
    Long countByAnneeScolaireIdAndStatut(Long anneeScolaireId, String statut);
    
    Optional<Facture> findByNumeroFacture(String numeroFacture);
    
    boolean existsByNumeroFacture(String numeroFacture);
    
    List<Facture> findByEleveId(Long eleveId);
    
    List<Facture> findByStatut(StatutFacture statut);
    
    List<Facture> findByTypeFacture(TypeFacture typeFacture);
    
    @Query("SELECT f FROM Facture f WHERE f.eleve.id = :eleveId AND f.statut != 'PAYEE'")
    List<Facture> findFacturesImpayeesByEleveId(@Param("eleveId") Long eleveId);
    
    @Query("SELECT f FROM Facture f WHERE f.dateEcheance < :date AND f.statut != 'PAYEE'")
    List<Facture> findFacturesEchues(@Param("date") LocalDate date);
    
    @Query("SELECT f FROM Facture f WHERE f.anneeScolaire.id = :anneeScolaireId")
    List<Facture> findByAnneeScolaireId(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT f FROM Facture f WHERE f.periode.id = :periodeId")
    List<Facture> findByPeriodeId(@Param("periodeId") Long periodeId);
    
    @Query("SELECT SUM(f.montantTotal) FROM Facture f WHERE f.anneeScolaire.id = :anneeScolaireId")
    BigDecimal getTotalFactureByAnneeScolaire(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT SUM(f.montantPaye) FROM Facture f WHERE f.anneeScolaire.id = :anneeScolaireId")
    BigDecimal getTotalPayeByAnneeScolaire(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT SUM(f.montantRestant) FROM Facture f WHERE f.anneeScolaire.id = :anneeScolaireId")
    BigDecimal getTotalRestantByAnneeScolaire(@Param("anneeScolaireId") Long anneeScolaireId);
    
    @Query("SELECT f FROM Facture f WHERE f.rappelEnvoye = false AND f.dateEcheance < :date AND f.statut != 'PAYEE'")
    List<Facture> findFacturesForRappel(@Param("date") LocalDate date);
    
    Page<Facture> findByEleveIdOrderByDateEmissionDesc(Long eleveId, Pageable pageable);
}
