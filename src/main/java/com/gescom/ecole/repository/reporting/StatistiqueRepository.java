package com.gescom.ecole.repository.reporting;

import com.gescom.ecole.entity.reporting.Statistique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatistiqueRepository extends JpaRepository<Statistique, Long> {
    
    // Recherche par type et période
    Optional<Statistique> findByTypeStatistiqueAndPeriodeAndDateDebutAndDateFin(
            String typeStatistique, String periode, LocalDate dateDebut, LocalDate dateFin);
    
    // Statistiques par catégorie
    List<Statistique> findByCategorieOrderByDateCalculDesc(String categorie);
    
    // Statistiques par entité
    List<Statistique> findByEntiteLieeTypeAndEntiteLieeId(String entiteLieeType, Long entiteLieeId);
    
    // Statistiques par année scolaire
    List<Statistique> findByAnneeScolaireIdOrderByDateCalculDesc(Long anneeScolaireId);
    
    // Statistiques récentes
    List<Statistique> findByDateCalculAfterOrderByDateCalculDesc(LocalDateTime dateCalcul);
    
    // Statistiques par période
    @Query("SELECT s FROM Statistique s WHERE s.dateDebut >= :debut AND s.dateFin <= :fin " +
           "ORDER BY s.dateCalcul DESC")
    List<Statistique> findStatistiquesParPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
    
    // Dernière statistique calculée pour un type
    Optional<Statistique> findTopByTypeStatistiqueOrderByDateCalculDesc(String typeStatistique);
    
    // Statistiques valides (non expirées)
    @Query("SELECT s FROM Statistique s WHERE s.dateCalcul > :dateExpiration " +
           "ORDER BY s.dateCalcul DESC")
    List<Statistique> findStatistiquesValides(@Param("dateExpiration") LocalDateTime dateExpiration);
    
    // Supprimer les statistiques anciennes
    @Modifying
    @Query("DELETE FROM Statistique s WHERE s.dateCalcul < :dateExpiration")
    int supprimerStatistiquesAnciennes(@Param("dateExpiration") LocalDateTime dateExpiration);
    
    // Statistiques pour dashboard
    @Query("SELECT s FROM Statistique s WHERE s.categorie = 'DASHBOARD' " +
           "AND s.anneeScolaireId = :anneeScolaireId " +
           "AND s.dateCalcul > :dateExpiration")
    List<Statistique> findStatistiquesDashboard(@Param("anneeScolaireId") Long anneeScolaireId,
                                                @Param("dateExpiration") LocalDateTime dateExpiration);
    
    // Vérifier si une statistique existe et est valide
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Statistique s WHERE s.typeStatistique = :type " +
           "AND s.anneeScolaireId = :anneeScolaireId " +
           "AND s.dateCalcul > :dateExpiration")
    boolean existsValidStatistique(@Param("type") String type, 
                                  @Param("anneeScolaireId") Long anneeScolaireId,
                                  @Param("dateExpiration") LocalDateTime dateExpiration);
}
