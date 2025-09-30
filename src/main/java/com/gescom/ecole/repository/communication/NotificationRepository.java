package com.gescom.ecole.repository.communication;

import com.gescom.ecole.common.enums.TypeNotification;
import com.gescom.ecole.entity.communication.Notification;
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
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Notifications par utilisateur
    Page<Notification> findByUtilisateurIdOrderByDateEnvoiDesc(Long utilisateurId, Pageable pageable);
    
    // Notifications non lues
    Page<Notification> findByUtilisateurIdAndLuFalseOrderByPrioriteDescDateEnvoiDesc(Long utilisateurId, Pageable pageable);
    
    // Notifications par type
    Page<Notification> findByUtilisateurIdAndTypeOrderByDateEnvoiDesc(Long utilisateurId, TypeNotification type, Pageable pageable);
    
    // Notifications importantes
    Page<Notification> findByUtilisateurIdAndPrioriteGreaterThanEqualOrderByDateEnvoiDesc(Long utilisateurId, Integer priorite, Pageable pageable);
    
    // Compter les notifications non lues
    Long countByUtilisateurIdAndLuFalse(Long utilisateurId);
    
    // Notifications non expirées
    @Query("SELECT n FROM Notification n WHERE n.utilisateur.id = :userId " +
           "AND (n.expireLe IS NULL OR n.expireLe > :maintenant) " +
           "ORDER BY n.priorite DESC, n.dateEnvoi DESC")
    Page<Notification> findNotificationsActives(@Param("userId") Long userId, 
                                                @Param("maintenant") LocalDateTime maintenant, 
                                                Pageable pageable);
    
    // Marquer comme lues
    @Modifying
    @Query("UPDATE Notification n SET n.lu = true, n.dateLecture = :dateLecture " +
           "WHERE n.utilisateur.id = :userId AND n.lu = false")
    int marquerToutesCommeLues(@Param("userId") Long userId, @Param("dateLecture") LocalDateTime dateLecture);
    
    // Marquer une notification comme lue
    @Modifying
    @Query("UPDATE Notification n SET n.lu = true, n.dateLecture = :dateLecture " +
           "WHERE n.id = :notificationId")
    int marquerCommeLue(@Param("notificationId") Long notificationId, @Param("dateLecture") LocalDateTime dateLecture);
    
    // Supprimer les notifications expirées
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expireLe IS NOT NULL AND n.expireLe < :maintenant")
    int supprimerNotificationsExpirees(@Param("maintenant") LocalDateTime maintenant);
    
    // Notifications récentes
    List<Notification> findTop10ByUtilisateurIdOrderByDateEnvoiDesc(Long utilisateurId);
    
    // Notifications par période
    List<Notification> findByUtilisateurIdAndDateEnvoiBetween(Long utilisateurId, LocalDateTime debut, LocalDateTime fin);
    
    // Recherche dans les notifications
    @Query("SELECT n FROM Notification n WHERE n.utilisateur.id = :userId " +
           "AND (LOWER(n.titre) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
           "OR LOWER(n.message) LIKE LOWER(CONCAT('%', :recherche, '%'))) " +
           "ORDER BY n.dateEnvoi DESC")
    Page<Notification> searchNotifications(@Param("userId") Long userId, 
                                          @Param("recherche") String recherche, 
                                          Pageable pageable);
}
