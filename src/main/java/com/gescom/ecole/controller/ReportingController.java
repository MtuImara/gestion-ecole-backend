package com.gescom.ecole.controller;

import com.gescom.ecole.dto.reporting.DashboardStatsDTO;
import com.gescom.ecole.dto.reporting.StatistiqueDTO;
import com.gescom.ecole.dto.reporting.StatistiqueFinanciereDTO;
import com.gescom.ecole.dto.reporting.StatistiqueScolaireDTO;
import com.gescom.ecole.service.StatistiqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reporting")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reporting", description = "API de gestion des rapports et statistiques")
public class ReportingController {

    private final StatistiqueService statistiqueService;

    @GetMapping("/dashboard")
    @Operation(summary = "Obtenir les statistiques du dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'COMPTABLE')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(
            @RequestParam(required = false) Long anneeScolaireId) {
        log.info("Récupération des statistiques du dashboard");
        DashboardStatsDTO stats = anneeScolaireId != null 
            ? statistiqueService.getDashboardStatsByAnneeScolaire(anneeScolaireId)
            : statistiqueService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/scolaire")
    @Operation(summary = "Obtenir les statistiques scolaires")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT')")
    public ResponseEntity<StatistiqueScolaireDTO> getStatistiquesScolaires(
            @RequestParam Long anneeScolaireId) {
        log.info("Récupération des statistiques scolaires pour l'année {}", anneeScolaireId);
        return ResponseEntity.ok(statistiqueService.getStatistiquesScolaires(anneeScolaireId));
    }

    @GetMapping("/financier")
    @Operation(summary = "Obtenir les statistiques financières")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'COMPTABLE')")
    public ResponseEntity<StatistiqueFinanciereDTO> getStatistiquesFinancieres(
            @RequestParam Long anneeScolaireId) {
        log.info("Récupération des statistiques financières pour l'année {}", anneeScolaireId);
        return ResponseEntity.ok(statistiqueService.getStatistiquesFinancieres(anneeScolaireId));
    }

    @GetMapping("/taux-reussite")
    @Operation(summary = "Obtenir le taux de réussite par classe")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT')")
    public ResponseEntity<Map<String, Object>> getTauxReussiteParClasse(
            @RequestParam Long anneeScolaireId,
            @RequestParam(required = false) Long periodeId) {
        log.info("Récupération du taux de réussite par classe");
        return ResponseEntity.ok(statistiqueService.getTauxReussiteParClasse(anneeScolaireId, periodeId));
    }

    @GetMapping("/taux-presence")
    @Operation(summary = "Obtenir le taux de présence par classe")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT')")
    public ResponseEntity<Map<String, Object>> getTauxPresenceParClasse(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("Récupération du taux de présence du {} au {}", debut, fin);
        return ResponseEntity.ok(statistiqueService.getTauxPresenceParClasse(debut, fin));
    }

    @GetMapping("/evolution-effectifs")
    @Operation(summary = "Obtenir l'évolution des effectifs")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<Map<String, Object>> getEvolutionEffectifs(
            @RequestParam Long anneeScolaireId) {
        log.info("Récupération de l'évolution des effectifs");
        return ResponseEntity.ok(statistiqueService.getEvolutionEffectifs(anneeScolaireId));
    }

    @GetMapping("/top-eleves")
    @Operation(summary = "Obtenir le top 10 des élèves")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT')")
    public ResponseEntity<List<Map<String, Object>>> getTop10Eleves(
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) Long periodeId) {
        log.info("Récupération du top 10 des élèves");
        return ResponseEntity.ok(statistiqueService.getTop10Eleves(classeId, periodeId));
    }

    @GetMapping("/evolution-recettes")
    @Operation(summary = "Obtenir l'évolution des recettes")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'COMPTABLE')")
    public ResponseEntity<Map<String, Object>> getEvolutionRecettes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("Récupération de l'évolution des recettes du {} au {}", debut, fin);
        return ResponseEntity.ok(statistiqueService.getEvolutionRecettes(debut, fin));
    }

    @GetMapping("/taux-recouvrement")
    @Operation(summary = "Obtenir le taux de recouvrement")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'COMPTABLE')")
    public ResponseEntity<Map<String, Object>> getTauxRecouvrement(
            @RequestParam Long anneeScolaireId) {
        log.info("Récupération du taux de recouvrement");
        return ResponseEntity.ok(statistiqueService.getTauxRecouvrement(anneeScolaireId));
    }

    @GetMapping("/impayes")
    @Operation(summary = "Obtenir la liste des impayés")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'COMPTABLE')")
    public ResponseEntity<List<Map<String, Object>>> getImpayes(
            @RequestParam Long anneeScolaireId) {
        log.info("Récupération des impayés");
        return ResponseEntity.ok(statistiqueService.getImpayes(anneeScolaireId));
    }

    @GetMapping("/eleve/{eleveId}")
    @Operation(summary = "Obtenir les statistiques d'un élève")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT', 'PARENT')")
    public ResponseEntity<Map<String, Object>> getStatistiquesEleve(@PathVariable Long eleveId) {
        log.info("Récupération des statistiques de l'élève {}", eleveId);
        return ResponseEntity.ok(statistiqueService.getStatistiquesEleve(eleveId));
    }

    @GetMapping("/classe/{classeId}")
    @Operation(summary = "Obtenir les statistiques d'une classe")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'ENSEIGNANT')")
    public ResponseEntity<Map<String, Object>> getStatistiquesClasse(@PathVariable Long classeId) {
        log.info("Récupération des statistiques de la classe {}", classeId);
        return ResponseEntity.ok(statistiqueService.getStatistiquesClasse(classeId));
    }

    @GetMapping("/export")
    @Operation(summary = "Exporter des statistiques")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'COMPTABLE')")
    public ResponseEntity<byte[]> exportStatistiques(
            @RequestParam String type,
            @RequestParam String format,
            @RequestParam Map<String, Object> parametres) {
        log.info("Export des statistiques {} au format {}", type, format);
        
        byte[] data = statistiqueService.exportStatistiques(type, format, parametres);
        
        HttpHeaders headers = new HttpHeaders();
        String filename = String.format("statistiques_%s_%s.%s", 
            type, LocalDate.now(), format.toLowerCase());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        
        MediaType mediaType = switch (format.toUpperCase()) {
            case "PDF" -> MediaType.APPLICATION_PDF;
            case "EXCEL", "XLSX" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "CSV" -> MediaType.parseMediaType("text/csv");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
        
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(mediaType)
            .body(data);
    }

    @PostMapping("/calculer")
    @Operation(summary = "Calculer une statistique personnalisée")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatistiqueDTO> calculerStatistique(
            @RequestParam String type,
            @RequestBody Map<String, Object> parametres) {
        log.info("Calcul de statistique personnalisée de type {}", type);
        return ResponseEntity.ok(statistiqueService.calculerStatistique(type, parametres));
    }

    @PostMapping("/rafraichir")
    @Operation(summary = "Rafraîchir les statistiques")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rafraichirStatistiques() {
        log.info("Rafraîchissement des statistiques");
        statistiqueService.rafraichirStatistiques();
        return ResponseEntity.ok().build();
    }
}
