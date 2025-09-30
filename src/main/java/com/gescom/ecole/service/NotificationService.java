package com.gescom.ecole.service;

import com.gescom.ecole.common.enums.TypeNotification;
import com.gescom.ecole.dto.communication.NotificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface NotificationService {
    
    // CRUD de base
    NotificationDTO create(NotificationDTO notificationDTO);
    NotificationDTO findById(Long id);
    void delete(Long id);
    
    // Récupération des notifications
    Page<NotificationDTO> getNotificationsUtilisateur(Long userId, Pageable pageable);
    Page<NotificationDTO> getNotificationsNonLues(Long userId, Pageable pageable);
    Page<NotificationDTO> getNotificationsParType(Long userId, TypeNotification type, Pageable pageable);
    Page<NotificationDTO> getNotificationsImportantes(Long userId, Pageable pageable);
    List<NotificationDTO> getNotificationsRecentes(Long userId);
    
    // Gestion des notifications
    NotificationDTO marquerCommeLue(Long notificationId);
    int marquerToutesCommeLues(Long userId);
    
    // Notifications système
    void notifierPaiementRecu(Long eleveId, Long paiementId, String montant);
    void notifierNouvelleFacture(Long eleveId, Long factureId, String montant);
    void notifierAbsence(Long eleveId, LocalDateTime dateAbsence, String motif);
    void notifierNouvelleNote(Long eleveId, Long evaluationId, String matiere, String note);
    void notifierNouveauMessage(Long userId, Long messageId, String expediteur);
    void notifierNouvelleAnnonce(String typeDestinataire, Long annonceId, String titre);
    void notifierRappelPaiement(Long eleveId, Long factureId, String montant, LocalDateTime echeance);
    void notifierReunion(List<Long> userIds, String titre, LocalDateTime dateReunion);
    void notifierEvaluation(Long classeId, String matiere, LocalDateTime dateEvaluation);
    void notifierActionDisciplinaire(Long eleveId, String sanction, String motif);
    
    // Notifications groupées
    void notifierGroupe(List<Long> userIds, NotificationDTO notification);
    void notifierClasse(Long classeId, NotificationDTO notification);
    void notifierNiveau(Long niveauId, NotificationDTO notification);
    void notifierTousLesParents(NotificationDTO notification);
    void notifierTousLesEnseignants(NotificationDTO notification);
    
    // Recherche et filtres
    Page<NotificationDTO> searchNotifications(Long userId, String recherche, Pageable pageable);
    Page<NotificationDTO> getNotificationsActives(Long userId, Pageable pageable);
    
    // Statistiques
    Long countNotificationsNonLues(Long userId);
    Map<TypeNotification, Long> getStatistiquesParType(Long userId);
    
    // Maintenance
    int supprimerNotificationsExpirees();
    void nettoyerNotificationsAnciennes(Integer joursConservation);
    
    // Configuration
    void configurerPreferencesNotifications(Long userId, Map<TypeNotification, Boolean> preferences);
    Map<TypeNotification, Boolean> getPreferencesNotifications(Long userId);
}
