package com.gescom.ecole.controller;

import com.gescom.ecole.common.enums.TypeParent;
import com.gescom.ecole.dto.utilisateur.ParentDTO;
import com.gescom.ecole.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Parents", description = "API de gestion des parents")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ParentController {
    
    private final ParentService parentService;
    
    @PostMapping
    @Operation(summary = "Créer un nouveau parent")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<ParentDTO> createParent(@Valid @RequestBody ParentDTO parentDTO) {
        log.info("Création d'un nouveau parent: {} {}", parentDTO.getPrenom(), parentDTO.getNom());
        ParentDTO createdParent = parentService.create(parentDTO);
        return new ResponseEntity<>(createdParent, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un parent")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<ParentDTO> updateParent(
            @PathVariable Long id,
            @Valid @RequestBody ParentDTO parentDTO) {
        log.info("Mise à jour du parent avec l'ID: {}", id);
        ParentDTO updatedParent = parentService.update(id, parentDTO);
        return ResponseEntity.ok(updatedParent);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un parent par son ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT', 'COMPTABLE')")
    public ResponseEntity<ParentDTO> getParentById(@PathVariable Long id) {
        log.debug("Récupération du parent avec l'ID: {}", id);
        ParentDTO parent = parentService.findById(id);
        return ResponseEntity.ok(parent);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un parent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        log.info("Suppression du parent avec l'ID: {}", id);
        parentService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @Operation(summary = "Obtenir la liste des parents avec pagination")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT', 'COMPTABLE')")
    public ResponseEntity<Map<String, Object>> getAllParents(
            @Parameter(description = "Numéro de la page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "nom") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "ASC") String sortDirection,
            @Parameter(description = "Terme de recherche") @RequestParam(required = false) String search) {
        
        log.debug("Récupération des parents - page: {}, size: {}, search: {}", page, size, search);
        
        Sort sort = sortDirection.equalsIgnoreCase("DESC") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ParentDTO> parentsPage;
        if (search != null && !search.trim().isEmpty()) {
            parentsPage = parentService.search(search, pageable);
        } else {
            parentsPage = parentService.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", parentsPage.getContent());
        response.put("currentPage", parentsPage.getNumber());
        response.put("totalItems", parentsPage.getTotalElements());
        response.put("totalPages", parentsPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    @Operation(summary = "Obtenir tous les parents sans pagination")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT', 'COMPTABLE')")
    public ResponseEntity<List<ParentDTO>> getAllParentsNoPagination() {
        log.debug("Récupération de tous les parents sans pagination");
        List<ParentDTO> parents = parentService.findAll();
        return ResponseEntity.ok(parents);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Rechercher des parents")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT', 'COMPTABLE')")
    public ResponseEntity<List<ParentDTO>> searchParents(
            @Parameter(description = "Terme de recherche") @RequestParam String search) {
        log.debug("Recherche de parents avec le terme: {}", search);
        List<ParentDTO> parents = parentService.searchSimple(search);
        return ResponseEntity.ok(parents);
    }
    
    @GetMapping("/type/{typeParent}")
    @Operation(summary = "Obtenir les parents par type")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<List<ParentDTO>> getParentsByType(@PathVariable TypeParent typeParent) {
        log.debug("Récupération des parents de type: {}", typeParent);
        List<ParentDTO> parents = parentService.findByTypeParent(typeParent);
        return ResponseEntity.ok(parents);
    }
    
    @GetMapping("/eleve/{eleveId}")
    @Operation(summary = "Obtenir les parents d'un élève")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT')")
    public ResponseEntity<List<ParentDTO>> getParentsByEleveId(@PathVariable Long eleveId) {
        log.debug("Récupération des parents de l'élève: {}", eleveId);
        List<ParentDTO> parents = parentService.findByEleveId(eleveId);
        return ResponseEntity.ok(parents);
    }
    
    @PostMapping("/{parentId}/enfants/{eleveId}")
    @Operation(summary = "Ajouter un enfant à un parent")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<Void> addEnfantToParent(
            @PathVariable Long parentId,
            @PathVariable Long eleveId) {
        log.info("Ajout de l'élève {} au parent {}", eleveId, parentId);
        parentService.addEnfantToParent(parentId, eleveId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{parentId}/enfants/{eleveId}")
    @Operation(summary = "Retirer un enfant d'un parent")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<Void> removeEnfantFromParent(
            @PathVariable Long parentId,
            @PathVariable Long eleveId) {
        log.info("Retrait de l'élève {} du parent {}", eleveId, parentId);
        parentService.removeEnfantFromParent(parentId, eleveId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/exists/email/{email}")
    @Operation(summary = "Vérifier si un email existe")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = parentService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/exists/cin/{cin}")
    @Operation(summary = "Vérifier si un CIN existe")
    public ResponseEntity<Boolean> checkCinExists(@PathVariable String cin) {
        boolean exists = parentService.existsByCin(cin);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/count")
    @Operation(summary = "Obtenir le nombre total de parents")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<Long> getParentsCount() {
        long count = parentService.count();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/generate-numero")
    @Operation(summary = "Générer un nouveau numéro parent")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<Map<String, String>> generateNumeroParent() {
        String numero = parentService.generateNumeroParent();
        Map<String, String> response = new HashMap<>();
        response.put("numeroParent", numero);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/situation-financiere")
    @Operation(summary = "Obtenir la situation financière d'un parent")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'COMPTABLE', 'PARENT')")
    public ResponseEntity<Map<String, Object>> getSituationFinanciere(@PathVariable Long id) {
        log.debug("Récupération de la situation financière du parent: {}", id);
        Map<String, Object> situation = parentService.getSituationFinanciere(id);
        return ResponseEntity.ok(situation);
    }
}
