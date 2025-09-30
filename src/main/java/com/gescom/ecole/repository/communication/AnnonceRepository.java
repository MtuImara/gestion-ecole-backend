package com.gescom.ecole.repository.communication;

import com.gescom.ecole.common.enums.TypeAnnonce;
import com.gescom.ecole.entity.communication.Annonce;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    
    // Annonces actives
    @Query("SELECT a FROM Annonce a WHERE a.active = true " +
           "AND a.dateDebutAffichage <= :maintenant " +
           "AND (a.dateFinAffichage IS NULL OR a.dateFinAffichage >= :maintenant) " +
           "ORDER BY a.epinglee DESC, a.priorite DESC, a.datePublication DESC")
    Page<Annonce> findAnnoncesActives(@Param("maintenant") LocalDateTime maintenant, Pageable pageable);
    
    // Annonces épinglées
    Page<Annonce> findByActiveAndEpingleeOrderByPrioriteDescDatePublicationDesc(
            Boolean active, Boolean epinglee, Pageable pageable);
    
    // Annonces par type
    Page<Annonce> findByTypeAndActiveOrderByDatePublicationDesc(TypeAnnonce type, Boolean active, Pageable pageable);
    
    // Annonces par auteur
    Page<Annonce> findByAuteurIdOrderByDatePublicationDesc(Long auteurId, Pageable pageable);
    
    // Annonces pour un destinataire
    @Query("SELECT a FROM Annonce a JOIN a.destinataires d " +
           "WHERE a.active = true AND (d = 'TOUS' OR d = :destinataire) " +
           "AND a.dateDebutAffichage <= :maintenant " +
           "AND (a.dateFinAffichage IS NULL OR a.dateFinAffichage >= :maintenant) " +
           "ORDER BY a.epinglee DESC, a.priorite DESC, a.datePublication DESC")
    Page<Annonce> findAnnoncesForDestinataire(@Param("destinataire") String destinataire, 
                                              @Param("maintenant") LocalDateTime maintenant, 
                                              Pageable pageable);
    
    // Recherche d'annonces
    @Query("SELECT a FROM Annonce a WHERE a.active = true " +
           "AND (LOWER(a.titre) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
           "OR LOWER(a.contenu) LIKE LOWER(CONCAT('%', :recherche, '%'))) " +
           "ORDER BY a.datePublication DESC")
    Page<Annonce> searchAnnonces(@Param("recherche") String recherche, Pageable pageable);
    
    // Annonces à venir
    @Query("SELECT a FROM Annonce a WHERE a.active = true " +
           "AND a.dateDebutAffichage > :maintenant " +
           "ORDER BY a.dateDebutAffichage ASC")
    List<Annonce> findAnnoncesFutures(@Param("maintenant") LocalDateTime maintenant);
    
    // Annonces expirées
    @Query("SELECT a FROM Annonce a WHERE a.active = true " +
           "AND a.dateFinAffichage IS NOT NULL " +
           "AND a.dateFinAffichage < :maintenant")
    List<Annonce> findAnnoncesExpirees(@Param("maintenant") LocalDateTime maintenant);
    
    // Incrémenter le nombre de vues
    @Modifying
    @Query("UPDATE Annonce a SET a.nombreVues = a.nombreVues + 1 WHERE a.id = :annonceId")
    int incrementerVues(@Param("annonceId") Long annonceId);
    
    // Désactiver les annonces expirées
    @Modifying
    @Query("UPDATE Annonce a SET a.active = false " +
           "WHERE a.active = true AND a.dateFinAffichage IS NOT NULL " +
           "AND a.dateFinAffichage < :maintenant")
    int desactiverAnnoncesExpirees(@Param("maintenant") LocalDateTime maintenant);
    
    // Compter les annonces actives
    @Query("SELECT COUNT(a) FROM Annonce a WHERE a.active = true " +
           "AND a.dateDebutAffichage <= :maintenant " +
           "AND (a.dateFinAffichage IS NULL OR a.dateFinAffichage >= :maintenant)")
    Long countAnnoncesActives(@Param("maintenant") LocalDateTime maintenant);
    
    // Top annonces les plus vues
    List<Annonce> findTop10ByActiveOrderByNombreVuesDesc(Boolean active);
}
