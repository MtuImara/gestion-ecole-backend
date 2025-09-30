package com.gescom.ecole.controller;

import com.gescom.ecole.dto.finance.PaiementDTO;
import com.gescom.ecole.service.PaiementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@Tag(name = "Paiements", description = "Gestion des paiements")
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    @Operation(summary = "Créer un paiement", description = "Enregistre un nouveau paiement")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'PARENT')")
    public ResponseEntity<PaiementDTO> create(@Valid @RequestBody PaiementDTO paiementDTO) {
        PaiementDTO created = paiementService.create(paiementDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un paiement", description = "Met à jour un paiement existant")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<PaiementDTO> update(@PathVariable Long id, @Valid @RequestBody PaiementDTO paiementDTO) {
        PaiementDTO updated = paiementService.update(id, paiementDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un paiement", description = "Récupère un paiement par son ID")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'PARENT')")
    public ResponseEntity<PaiementDTO> findById(@PathVariable Long id) {
        PaiementDTO paiement = paiementService.findById(id);
        return ResponseEntity.ok(paiement);
    }

    @GetMapping("/numero/{numeroPaiement}")
    @Operation(summary = "Obtenir un paiement par numéro", description = "Récupère un paiement par son numéro")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<PaiementDTO> findByNumeroPaiement(@PathVariable String numeroPaiement) {
        PaiementDTO paiement = paiementService.findByNumeroPaiement(numeroPaiement);
        return ResponseEntity.ok(paiement);
    }

    @GetMapping
    @Operation(summary = "Liste des paiements", description = "Récupère la liste paginée des paiements")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<Page<PaiementDTO>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<PaiementDTO> paiements = paiementService.findAll(pageable);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/facture/{factureId}")
    @Operation(summary = "Paiements d'une facture", description = "Récupère tous les paiements d'une facture")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'PARENT')")
    public ResponseEntity<List<PaiementDTO>> findByFactureId(@PathVariable Long factureId) {
        List<PaiementDTO> paiements = paiementService.findByFactureId(factureId);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/parent/{parentId}")
    @Operation(summary = "Paiements d'un parent", description = "Récupère tous les paiements effectués par un parent")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'PARENT')")
    public ResponseEntity<List<PaiementDTO>> findByParentId(@PathVariable Long parentId) {
        List<PaiementDTO> paiements = paiementService.findByParentId(parentId);
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/periode")
    @Operation(summary = "Paiements par période", description = "Récupère les paiements sur une période donnée")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<List<PaiementDTO>> findByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        List<PaiementDTO> paiements = paiementService.findByPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(paiements);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un paiement", description = "Supprime un paiement")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paiementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/valider")
    @Operation(summary = "Valider un paiement", description = "Valide un paiement en attente")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<PaiementDTO> valider(@PathVariable Long id) {
        PaiementDTO paiement = paiementService.validerPaiement(id);
        return ResponseEntity.ok(paiement);
    }

    @PostMapping("/{id}/annuler")
    @Operation(summary = "Annuler un paiement", description = "Annule un paiement")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<PaiementDTO> annuler(@PathVariable Long id) {
        PaiementDTO paiement = paiementService.annulerPaiement(id);
        return ResponseEntity.ok(paiement);
    }

    @GetMapping("/{id}/recu")
    @Operation(summary = "Télécharger le reçu", description = "Génère et télécharge le reçu de paiement")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'PARENT')")
    public ResponseEntity<byte[]> genererRecu(@PathVariable Long id) {
        byte[] recu = paiementService.genererRecu(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "recu_" + id + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(recu);
    }

    @GetMapping("/generate-numero")
    @Operation(summary = "Générer un numéro de paiement", description = "Génère un nouveau numéro de paiement unique")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<Map<String, String>> generateNumero() {
        String numero = paiementService.generateNumeroPaiement();
        return ResponseEntity.ok(Map.of("numeroPaiement", numero));
    }

    @GetMapping("/statistiques/periode")
    @Operation(summary = "Statistiques par période", description = "Calcule le total des paiements sur une période")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<Map<String, BigDecimal>> getStatistiquesPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        BigDecimal total = paiementService.getTotalPaiementsByPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(Map.of("totalPaiements", total));
    }
}
