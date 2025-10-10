package com.gescom.ecole.controller;

import com.gescom.ecole.dto.scolaire.ClasseDTO;
import com.gescom.ecole.service.ClasseService;
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
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "Classes", description = "Gestion des classes")
public class ClasseController {

    private final ClasseService classeService;

    @PostMapping
    @Operation(summary = "Créer une classe", description = "Crée une nouvelle classe")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<ClasseDTO> create(@Valid @RequestBody ClasseDTO classeDTO) {
        ClasseDTO created = classeService.create(classeDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une classe", description = "Met à jour les informations d'une classe")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<ClasseDTO> update(@PathVariable Long id, @Valid @RequestBody ClasseDTO classeDTO) {
        ClasseDTO updated = classeService.update(id, classeDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une classe", description = "Récupère une classe par son ID")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<ClasseDTO> findById(@PathVariable Long id) {
        ClasseDTO classe = classeService.findById(id);
        return ResponseEntity.ok(classe);
    }

    @GetMapping
    @Operation(summary = "Liste des classes", description = "Récupère la liste paginée des classes")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<Page<ClasseDTO>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<ClasseDTO> classes = classeService.findAll(pageable);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/niveau/{niveauId}")
    @Operation(summary = "Classes par niveau", description = "Récupère les classes d'un niveau")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<List<ClasseDTO>> findByNiveauId(@PathVariable Long niveauId) {
        List<ClasseDTO> classes = classeService.findByNiveauId(niveauId);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/annee-scolaire/{anneeScolaireId}")
    @Operation(summary = "Classes par année scolaire", description = "Récupère les classes d'une année scolaire")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<List<ClasseDTO>> findByAnneeScolaireId(@PathVariable Long anneeScolaireId) {
        List<ClasseDTO> classes = classeService.findByAnneeScolaireId(anneeScolaireId);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/actives")
    @Operation(summary = "Classes actives", description = "Récupère toutes les classes actives")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE', 'ENSEIGNANT')")
    public ResponseEntity<List<ClasseDTO>> findActiveClasses() {
        List<ClasseDTO> classes = classeService.findActiveClasses();
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Classes disponibles", description = "Récupère les classes ayant des places disponibles")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<List<ClasseDTO>> findClassesWithAvailableSpace() {
        List<ClasseDTO> classes = classeService.findClassesWithAvailableSpace();
        return ResponseEntity.ok(classes);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une classe", description = "Supprime une classe")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        classeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activer")
    @Operation(summary = "Activer une classe", description = "Active une classe")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<ClasseDTO> activer(@PathVariable Long id) {
        ClasseDTO classe = classeService.activerClasse(id);
        return ResponseEntity.ok(classe);
    }

    @PostMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver une classe", description = "Désactive une classe")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<ClasseDTO> desactiver(@PathVariable Long id) {
        ClasseDTO classe = classeService.desactiverClasse(id);
        return ResponseEntity.ok(classe);
    }

    @GetMapping("/{id}/est-pleine")
    @Operation(summary = "Vérifier si pleine", description = "Vérifie si une classe est pleine")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<Map<String, Boolean>> estPleine(@PathVariable Long id) {
        boolean pleine = classeService.estPleine(id);
        return ResponseEntity.ok(Map.of("estPleine", pleine));
    }

    @GetMapping("/effectif-total/{anneeScolaireId}")
    @Operation(summary = "Effectif total", description = "Calcule l'effectif total pour une année scolaire")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETAIRE')")
    public ResponseEntity<Map<String, Integer>> getEffectifTotal(@PathVariable Long anneeScolaireId) {
        Integer effectif = classeService.getEffectifTotal(anneeScolaireId);
        return ResponseEntity.ok(Map.of("effectifTotal", effectif));
    }
}
