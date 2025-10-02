package com.gescom.ecole.controller;

import com.gescom.ecole.common.enums.StatutDerogation;
import com.gescom.ecole.dto.finance.DerogationDTO;
import com.gescom.ecole.service.DerogationService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/derogations")
@RequiredArgsConstructor
@Tag(name = "Dérogations", description = "Gestion des dérogations financières")
public class DerogationController {

    private final DerogationService derogationService;

    @PostMapping
    @Operation(summary = "Créer une dérogation", description = "Crée une nouvelle demande de dérogation")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PARENT', 'ELEVE')")
    public ResponseEntity<DerogationDTO> create(@Valid @RequestBody DerogationDTO derogationDTO) {
        DerogationDTO created = derogationService.create(derogationDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une dérogation", description = "Modifie une dérogation en attente")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PARENT')")
    public ResponseEntity<DerogationDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DerogationDTO derogationDTO) {
        DerogationDTO updated = derogationService.update(id, derogationDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une dérogation", description = "Récupère une dérogation par son ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DerogationDTO> findById(@PathVariable Long id) {
        DerogationDTO derogation = derogationService.findById(id);
        return ResponseEntity.ok(derogation);
    }

    @GetMapping
    @Operation(summary = "Liste des dérogations", description = "Récupère toutes les dérogations (paginées)")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'SECRETAIRE')")
    public ResponseEntity<Page<DerogationDTO>> findAll(@PageableDefault Pageable pageable) {
        Page<DerogationDTO> derogations = derogationService.findAll(pageable);
        return ResponseEntity.ok(derogations);
    }

    @GetMapping("/eleve/{eleveId}")
    @Operation(summary = "Dérogations d'un élève", description = "Récupère toutes les dérogations d'un élève")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PARENT', 'ELEVE')")
    public ResponseEntity<List<DerogationDTO>> findByEleve(@PathVariable Long eleveId) {
        List<DerogationDTO> derogations = derogationService.findByEleveId(eleveId);
        return ResponseEntity.ok(derogations);
    }

    @GetMapping("/parent/{parentId}")
    @Operation(summary = "Dérogations d'un parent", description = "Récupère toutes les dérogations d'un parent")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PARENT')")
    public ResponseEntity<List<DerogationDTO>> findByParent(@PathVariable Long parentId) {
        List<DerogationDTO> derogations = derogationService.findByParentId(parentId);
        return ResponseEntity.ok(derogations);
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Dérogations par statut", description = "Récupère les dérogations selon leur statut")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'SECRETAIRE')")
    public ResponseEntity<Page<DerogationDTO>> findByStatut(
            @PathVariable StatutDerogation statut,
            @PageableDefault Pageable pageable) {
        Page<DerogationDTO> derogations = derogationService.findByStatut(statut, pageable);
        return ResponseEntity.ok(derogations);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une dérogation", description = "Supprime une dérogation en attente")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PARENT')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        derogationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approuver")
    @Operation(summary = "Approuver une dérogation", description = "Approuve une dérogation en attente")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<DerogationDTO> approuver(
            @PathVariable Long id,
            @RequestParam String decidePar) {
        DerogationDTO derogation = derogationService.approuverDerogation(id, decidePar);
        return ResponseEntity.ok(derogation);
    }

    @PostMapping("/{id}/rejeter")
    @Operation(summary = "Rejeter une dérogation", description = "Rejette une dérogation en attente")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<DerogationDTO> rejeter(
            @PathVariable Long id,
            @RequestParam String decidePar,
            @RequestParam String motifRejet) {
        DerogationDTO derogation = derogationService.rejeterDerogation(id, decidePar, motifRejet);
        return ResponseEntity.ok(derogation);
    }

    @GetMapping("/generate-numero")
    @Operation(summary = "Générer un numéro", description = "Génère un nouveau numéro de dérogation unique")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PARENT', 'ELEVE')")
    public ResponseEntity<Map<String, String>> generateNumero() {
        String numero = derogationService.generateNumeroDerogation();
        return ResponseEntity.ok(Map.of("numeroDerogation", numero));
    }

    @GetMapping("/count-par-statut")
    @Operation(summary = "Compter par statut", description = "Compte le nombre de dérogations par statut")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public ResponseEntity<Map<String, Long>> countParStatut() {
        Long enAttente = derogationService.countByStatut(StatutDerogation.EN_ATTENTE);
        Long approuvees = derogationService.countByStatut(StatutDerogation.APPROUVEE);
        Long rejetees = derogationService.countByStatut(StatutDerogation.REJETEE);
        
        return ResponseEntity.ok(Map.of(
            "enAttente", enAttente,
            "approuvees", approuvees,
            "rejetees", rejetees
        ));
    }
}
