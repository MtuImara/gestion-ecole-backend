package com.gescom.ecole.controller;

import com.gescom.ecole.dto.scolaire.EleveDTO;
import com.gescom.ecole.dto.scolaire.EleveStatistiquesDTO;
import com.gescom.ecole.service.EleveService;
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
@RequestMapping("/eleves")
@RequiredArgsConstructor
@Tag(name = "Élèves", description = "Gestion des élèves")
public class EleveController {

    private final EleveService eleveService;

    @PostMapping
    @Operation(summary = "Créer un élève", description = "Crée un nouvel élève")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<EleveDTO> create(@Valid @RequestBody EleveDTO eleveDTO) {
        EleveDTO created = eleveService.create(eleveDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un élève", description = "Met à jour les informations d'un élève")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<EleveDTO> update(@PathVariable Long id, @Valid @RequestBody EleveDTO eleveDTO) {
        EleveDTO updated = eleveService.update(id, eleveDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un élève", description = "Récupère un élève par son ID")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT', 'PARENT')")
    public ResponseEntity<EleveDTO> findById(@PathVariable Long id) {
        EleveDTO eleve = eleveService.findById(id);
        return ResponseEntity.ok(eleve);
    }

    @GetMapping("/matricule/{matricule}")
    @Operation(summary = "Obtenir un élève par matricule", description = "Récupère un élève par son matricule")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<EleveDTO> findByMatricule(@PathVariable String matricule) {
        EleveDTO eleve = eleveService.findByMatricule(matricule);
        return ResponseEntity.ok(eleve);
    }

    @GetMapping
    @Operation(summary = "Liste des élèves", description = "Récupère la liste paginée des élèves")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<Page<EleveDTO>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<EleveDTO> eleves = eleveService.findAll(pageable);
        return ResponseEntity.ok(eleves);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des élèves", description = "Recherche des élèves par nom, prénom ou matricule")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<Page<EleveDTO>> search(
            @RequestParam String search,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EleveDTO> eleves = eleveService.search(search, pageable);
        return ResponseEntity.ok(eleves);
    }

    @GetMapping("/classe/{classeId}")
    @Operation(summary = "Élèves par classe", description = "Récupère les élèves d'une classe")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<List<EleveDTO>> findByClasseId(@PathVariable Long classeId) {
        List<EleveDTO> eleves = eleveService.findByClasseId(classeId);
        return ResponseEntity.ok(eleves);
    }

    @GetMapping("/parent/{parentId}")
    @Operation(summary = "Enfants d'un parent", description = "Récupère les enfants d'un parent")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'PARENT')")
    public ResponseEntity<List<EleveDTO>> findByParentId(@PathVariable Long parentId) {
        List<EleveDTO> eleves = eleveService.findByParentId(parentId);
        return ResponseEntity.ok(eleves);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un élève", description = "Supprime un élève")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eleveService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eleveId}/inscrire/{classeId}")
    @Operation(summary = "Inscrire un élève", description = "Inscrit un élève dans une classe")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<EleveDTO> inscrire(
            @PathVariable Long eleveId,
            @PathVariable Long classeId) {
        EleveDTO eleve = eleveService.inscrireEleve(eleveId, classeId);
        return ResponseEntity.ok(eleve);
    }

    @PostMapping("/{eleveId}/transferer/{nouvelleClasseId}")
    @Operation(summary = "Transférer un élève", description = "Transfère un élève vers une nouvelle classe")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<EleveDTO> transferer(
            @PathVariable Long eleveId,
            @PathVariable Long nouvelleClasseId) {
        EleveDTO eleve = eleveService.transfererEleve(eleveId, nouvelleClasseId);
        return ResponseEntity.ok(eleve);
    }

    @GetMapping("/generate-matricule")
    @Operation(summary = "Générer un matricule", description = "Génère un nouveau matricule unique")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<Map<String, String>> generateMatricule() {
        String matricule = eleveService.generateMatricule();
        return ResponseEntity.ok(Map.of("matricule", matricule));
    }
    
    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques des élèves", description = "Récupère les statistiques globales des élèves")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<EleveStatistiquesDTO> getStatistiques() {
        EleveStatistiquesDTO stats = eleveService.getStatistiques();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Filtrer les élèves", description = "Filtre les élèves par classe, année scolaire et statut")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<Page<EleveDTO>> filter(
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) Long anneeScolaireId,
            @RequestParam(required = false) String statut,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EleveDTO> eleves = eleveService.filter(classeId, anneeScolaireId, statut, pageable);
        return ResponseEntity.ok(eleves);
    }
}
