package com.gescom.ecole.repository.communication;

import com.gescom.ecole.entity.communication.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Messages envoyés
    Page<Message> findByExpediteurIdOrderByDateEnvoiDesc(Long expediteurId, Pageable pageable);
    
    // Messages reçus
    @Query("SELECT m FROM Message m JOIN m.destinataires d WHERE d.id = :userId ORDER BY m.dateEnvoi DESC")
    Page<Message> findMessagesRecus(@Param("userId") Long userId, Pageable pageable);
    
    // Messages non lus
    @Query("SELECT m FROM Message m JOIN m.destinataires d LEFT JOIN m.lectures l " +
           "WHERE d.id = :userId AND (l.utilisateur.id != :userId OR l.lu = false OR l IS NULL) " +
           "ORDER BY m.dateEnvoi DESC")
    Page<Message> findMessagesNonLus(@Param("userId") Long userId, Pageable pageable);
    
    // Messages importants
    @Query("SELECT m FROM Message m JOIN m.destinataires d WHERE d.id = :userId AND m.important = true " +
           "ORDER BY m.dateEnvoi DESC")
    Page<Message> findMessagesImportants(@Param("userId") Long userId, Pageable pageable);
    
    // Brouillons
    Page<Message> findByExpediteurIdAndBrouillonTrueOrderByDateEnvoiDesc(Long expediteurId, Pageable pageable);
    
    // Recherche
    @Query("SELECT DISTINCT m FROM Message m LEFT JOIN m.destinataires d " +
           "WHERE (m.expediteur.id = :userId OR d.id = :userId) " +
           "AND (LOWER(m.objet) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
           "OR LOWER(m.contenu) LIKE LOWER(CONCAT('%', :recherche, '%'))) " +
           "ORDER BY m.dateEnvoi DESC")
    Page<Message> searchMessages(@Param("userId") Long userId, @Param("recherche") String recherche, Pageable pageable);
    
    // Messages archivés
    @Query("SELECT m FROM Message m JOIN m.lectures l " +
           "WHERE l.utilisateur.id = :userId AND l.archive = true " +
           "ORDER BY m.dateEnvoi DESC")
    Page<Message> findMessagesArchives(@Param("userId") Long userId, Pageable pageable);
    
    // Messages dans la corbeille
    @Query("SELECT m FROM Message m JOIN m.lectures l " +
           "WHERE l.utilisateur.id = :userId AND l.corbeille = true " +
           "ORDER BY m.dateEnvoi DESC")
    Page<Message> findMessagesCorbeille(@Param("userId") Long userId, Pageable pageable);
    
    // Compter les messages non lus
    @Query("SELECT COUNT(m) FROM Message m JOIN m.destinataires d LEFT JOIN m.lectures l " +
           "WHERE d.id = :userId AND (l.utilisateur.id != :userId OR l.lu = false OR l IS NULL)")
    Long countMessagesNonLus(@Param("userId") Long userId);
    
    // Messages par période
    List<Message> findByDateEnvoiBetweenOrderByDateEnvoiDesc(LocalDateTime debut, LocalDateTime fin);
    
    // Vérifier si un message existe avec pièces jointes
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
           "FROM Message m WHERE m.id = :messageId AND SIZE(m.piecesJointes) > 0")
    boolean hasPiecesJointes(@Param("messageId") Long messageId);
}
