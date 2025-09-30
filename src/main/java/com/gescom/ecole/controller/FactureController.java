package com.gescom.ecole.controller;

import com.gescom.ecole.dto.finance.FactureDTO;
import com.gescom.ecole.service.FactureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
@Tag(name = "Factures", description = "Gestion des factures")
public class FactureController {

    private final FactureService factureService;

    @PostMapping
    @Operation(summary = "Créer une facture", description = "Crée une nouvelle facture")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<FactureDTO> create(@Valid @RequestBody FactureDTO factureDTO) {
        FactureDTO created = factureService.create(factureDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une facture", description = "Met à jour une facture existante")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<FactureDTO> update(@PathVariable Long id, @Valid @RequestBody FactureDTO factureDTO) {
        FactureDTO updated = factureService.update(id, factureDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une facture", description = "Récupère une facture par son ID")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'PARENT')")
    public ResponseEntity<FactureDTO> findById(@PathVariable Long id) {
        FactureDTO facture = factureService.findById(id);
        return ResponseEntity.ok(facture);
    }

    @GetMapping("/numero/{numeroFacture}")
    @Operation(summary = "Obtenir une facture par numéro", description = "Récupère une facture par son numéro")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<FactureDTO> findByNumeroFacture(@PathVariable String numeroFacture) {
        FactureDTO facture = factureService.findByNumeroFacture(numeroFacture);
        return ResponseEntity.ok(facture);
    }

    @GetMapping
    @Operation(summary = "Liste des factures", description = "Récupère la liste paginée des factures")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<Page<FactureDTO>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<FactureDTO> factures = factureService.findAll(pageable);
        return ResponseEntity.ok(factures);
    }

    @GetMapping("/eleve/{eleveId}")
    @Operation(summary = "Factures d'un élève", description = "Récupère toutes les factures d'un élève")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'PARENT')")
    public ResponseEntity<List<FactureDTO>> findByEleveId(@PathVariable Long eleveId) {
        List<FactureDTO> factures = factureService.findByEleveId(eleveId);
        return ResponseEntity.ok(factures);
    }

    @GetMapping("/eleve/{eleveId}/impayees")
    @Operation(summary = "Factures impayées", description = "Récupère les factures impayées d'un élève")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'PARENT')")
    public ResponseEntity<List<FactureDTO>> findFacturesImpayees(@PathVariable Long eleveId) {
        List<FactureDTO> factures = factureService.findFacturesImpayees(eleveId);
        return ResponseEntity.ok(factures);
    }

    @GetMapping("/echues")
    @Operation(summary = "Factures échues", description = "Récupère toutes les factures échues")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<List<FactureDTO>> findFacturesEchues() {
        List<FactureDTO> factures = factureService.findFacturesEchues();
        return ResponseEntity.ok(factures);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une facture", description = "Supprime une facture")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        factureService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/valider")
    @Operation(summary = "Valider une facture", description = "Valide une facture en brouillon")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<FactureDTO> valider(@PathVariable Long id) {
        FactureDTO facture = factureService.validerFacture(id);
        return ResponseEntity.ok(facture);
    }

    @PostMapping("/{id}/annuler")
    @Operation(summary = "Annuler une facture", description = "Annule une facture")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<FactureDTO> annuler(@PathVariable Long id) {
        FactureDTO facture = factureService.annulerFacture(id);
        return ResponseEntity.ok(facture);
    }

    @PostMapping("/{id}/rappel")
    @Operation(summary = "Envoyer un rappel", description = "Envoie un rappel de paiement")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<Map<String, String>> envoyerRappel(@PathVariable Long id) {
        factureService.envoyerRappel(id);
        return ResponseEntity.ok(Map.of("message", "Rappel envoyé avec succès"));
    }

    @GetMapping("/generate-numero")
    @Operation(summary = "Générer un numéro de facture", description = "Génère un nouveau numéro de facture unique")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<Map<String, String>> generateNumero() {
        String numero = factureService.generateNumeroFacture();
        return ResponseEntity.ok(Map.of("numeroFacture", numero));
    }

    @GetMapping("/eleve/{eleveId}/solde")
    @Operation(summary = "Solde d'un élève", description = "Calcule le solde financier d'un élève")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'PARENT')")
    public ResponseEntity<Map<String, BigDecimal>> getSoldeEleve(@PathVariable Long eleveId) {
        BigDecimal montantTotal = factureService.calculerMontantTotal(eleveId);
        BigDecimal montantPaye = factureService.calculerMontantPaye(eleveId);
        BigDecimal montantRestant = factureService.calculerMontantRestant(eleveId);
        
        Map<String, BigDecimal> solde = Map.of(
            "montantTotal", montantTotal,
            "montantPaye", montantPaye,
            "montantRestant", montantRestant
        );
        
        return ResponseEntity.ok(solde);
    }
}
