package com.gescom.ecole.controller;

import com.gescom.ecole.common.enums.TypeAnnonce;
import com.gescom.ecole.common.enums.TypeNotification;
import com.gescom.ecole.dto.communication.AnnonceDTO;
import com.gescom.ecole.dto.communication.MessageDTO;
import com.gescom.ecole.dto.communication.NotificationDTO;
import com.gescom.ecole.service.AnnonceService;
import com.gescom.ecole.service.MessageService;
import com.gescom.ecole.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/communication")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Communication", description = "API de gestion de la communication")
public class CommunicationController {

    private final MessageService messageService;
    private final NotificationService notificationService;
    private final AnnonceService annonceService;

    // ========== MESSAGES ==========
    
    @PostMapping("/messages")
    @Operation(summary = "Envoyer un message")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDTO> envoyerMessage(
            @Valid @RequestPart("message") MessageDTO messageDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        log.info("Envoi d'un nouveau message");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(messageService.create(messageDTO, files));
    }

    @GetMapping("/messages/envoyes")
    @Operation(summary = "Obtenir les messages envoyés")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<MessageDTO>> getMessagesEnvoyes(
            Authentication auth, Pageable pageable) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(messageService.getMessagesEnvoyes(userId, pageable));
    }

    @GetMapping("/messages/recus")
    @Operation(summary = "Obtenir les messages reçus")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<MessageDTO>> getMessagesRecus(
            Authentication auth, Pageable pageable) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(messageService.getMessagesRecus(userId, pageable));
    }

    @GetMapping("/messages/non-lus")
    @Operation(summary = "Obtenir les messages non lus")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<MessageDTO>> getMessagesNonLus(
            Authentication auth, Pageable pageable) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(messageService.getMessagesNonLus(userId, pageable));
    }

    @GetMapping("/messages/{id}")
    @Operation(summary = "Obtenir un message par ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDTO> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @PutMapping("/messages/{id}/lire")
    @Operation(summary = "Marquer un message comme lu")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDTO> marquerCommeLu(
            @PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(messageService.marquerCommeLu(id, userId));
    }

    @PutMapping("/messages/{id}/archiver")
    @Operation(summary = "Archiver un message")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDTO> archiverMessage(
            @PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(messageService.archiverMessage(id, userId));
    }

    @DeleteMapping("/messages/{id}")
    @Operation(summary = "Supprimer un message")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> supprimerMessage(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/messages/count-non-lus")
    @Operation(summary = "Compter les messages non lus")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countMessagesNonLus(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(messageService.countMessagesNonLus(userId));
    }

    // ========== NOTIFICATIONS ==========
    
    @GetMapping("/notifications")
    @Operation(summary = "Obtenir les notifications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            Authentication auth, Pageable pageable) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(notificationService.getNotificationsUtilisateur(userId, pageable));
    }

    @GetMapping("/notifications/non-lues")
    @Operation(summary = "Obtenir les notifications non lues")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NotificationDTO>> getNotificationsNonLues(
            Authentication auth, Pageable pageable) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(userId, pageable));
    }

    @GetMapping("/notifications/{id}")
    @Operation(summary = "Obtenir une notification par ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @PutMapping("/notifications/{id}/lire")
    @Operation(summary = "Marquer une notification comme lue")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationDTO> marquerNotificationCommeLue(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.marquerCommeLue(id));
    }

    @PutMapping("/notifications/lire-toutes")
    @Operation(summary = "Marquer toutes les notifications comme lues")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Integer> marquerToutesNotificationsCommeLues(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(notificationService.marquerToutesCommeLues(userId));
    }

    @GetMapping("/notifications/count-non-lues")
    @Operation(summary = "Compter les notifications non lues")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countNotificationsNonLues(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(notificationService.countNotificationsNonLues(userId));
    }

    @GetMapping("/notifications/statistiques")
    @Operation(summary = "Obtenir les statistiques des notifications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<TypeNotification, Long>> getStatistiquesNotifications(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(notificationService.getStatistiquesParType(userId));
    }

    // ========== ANNONCES ==========
    
    @PostMapping("/annonces")
    @Operation(summary = "Créer une annonce")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<AnnonceDTO> creerAnnonce(
            @Valid @RequestPart("annonce") AnnonceDTO annonceDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        log.info("Création d'une nouvelle annonce");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(annonceService.create(annonceDTO, files));
    }

    @GetMapping("/annonces")
    @Operation(summary = "Obtenir les annonces actives")
    public ResponseEntity<Page<AnnonceDTO>> getAnnoncesActives(Pageable pageable) {
        return ResponseEntity.ok(annonceService.getAnnoncesActives(pageable));
    }

    @GetMapping("/annonces/epinglees")
    @Operation(summary = "Obtenir les annonces épinglées")
    public ResponseEntity<Page<AnnonceDTO>> getAnnoncesEpinglees(Pageable pageable) {
        return ResponseEntity.ok(annonceService.getAnnoncesEpinglees(pageable));
    }

    @GetMapping("/annonces/type/{type}")
    @Operation(summary = "Obtenir les annonces par type")
    public ResponseEntity<Page<AnnonceDTO>> getAnnoncesParType(
            @PathVariable TypeAnnonce type, Pageable pageable) {
        return ResponseEntity.ok(annonceService.getAnnoncesParType(type, pageable));
    }

    @GetMapping("/annonces/{id}")
    @Operation(summary = "Obtenir une annonce par ID")
    public ResponseEntity<AnnonceDTO> getAnnonce(@PathVariable Long id) {
        annonceService.incrementerVues(id);
        return ResponseEntity.ok(annonceService.findById(id));
    }

    @PutMapping("/annonces/{id}")
    @Operation(summary = "Modifier une annonce")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<AnnonceDTO> modifierAnnonce(
            @PathVariable Long id,
            @Valid @RequestBody AnnonceDTO annonceDTO) {
        return ResponseEntity.ok(annonceService.update(id, annonceDTO));
    }

    @PutMapping("/annonces/{id}/epingler")
    @Operation(summary = "Épingler une annonce")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<AnnonceDTO> epinglerAnnonce(@PathVariable Long id) {
        return ResponseEntity.ok(annonceService.epinglerAnnonce(id));
    }

    @PutMapping("/annonces/{id}/desepingler")
    @Operation(summary = "Désépingler une annonce")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<AnnonceDTO> desepinglerAnnonce(@PathVariable Long id) {
        return ResponseEntity.ok(annonceService.desepinglerAnnonce(id));
    }

    @DeleteMapping("/annonces/{id}")
    @Operation(summary = "Supprimer une annonce")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<Void> supprimerAnnonce(@PathVariable Long id) {
        annonceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/annonces/search")
    @Operation(summary = "Rechercher des annonces")
    public ResponseEntity<Page<AnnonceDTO>> searchAnnonces(
            @RequestParam String recherche, Pageable pageable) {
        return ResponseEntity.ok(annonceService.searchAnnonces(recherche, pageable));
    }

    @GetMapping("/annonces/count")
    @Operation(summary = "Compter les annonces actives")
    public ResponseEntity<Long> countAnnoncesActives() {
        return ResponseEntity.ok(annonceService.countAnnoncesActives());
    }

    // Méthode utilitaire pour extraire l'ID utilisateur
    private Long getUserId(Authentication auth) {
        // À adapter selon votre implémentation de l'authentification
        return Long.parseLong(auth.getName());
    }
}
