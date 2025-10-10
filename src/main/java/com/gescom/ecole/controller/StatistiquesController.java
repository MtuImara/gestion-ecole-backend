package com.gescom.ecole.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gescom.ecole.service.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class StatistiquesController {
    
    @Autowired
    private EleveService eleveService;
    
    @Autowired
    private PaiementService paiementService;
    
    @Autowired
    private ClasseService classeService;
    
    // Méthode supprimée - existe déjà dans EleveController
    // Pour obtenir les statistiques élèves, utiliser : GET /api/eleves/statistiques
    
    @GetMapping("/dashboard/paiements-stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'COMPTABLE')")
    public ResponseEntity<?> getDashboardStatistiquesPaiements() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMois", 42500000);
        stats.put("variationMois", 8.3);
        stats.put("tauxRecouvrement", 87.4);
        stats.put("variationTaux", 2.1);
        stats.put("nombreImpayes", 156);
        stats.put("variationImpayes", 5);
        return ResponseEntity.ok(stats);
    }
    
    // Statistiques comptable
    @GetMapping("/dashboard/comptable-stats")
    @PreAuthorize("hasRole('COMPTABLE')")
    public ResponseEntity<?> getStatistiquesComptable() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("recouvrementJour", 2500000);
        stats.put("nombrePaiementsJour", 23);
        stats.put("recouvrementMois", 42500000);
        stats.put("pourcentageVariation", 8.3);
        stats.put("nombreEnAttente", 12);
        stats.put("montantEnAttente", 3800000);
        stats.put("tauxRecouvrement", 87.4);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/dashboard/stats-par-mode-paiement")
    @PreAuthorize("hasRole('COMPTABLE')")
    public ResponseEntity<?> getStatistiquesParMode() {
        List<Map<String, Object>> modes = List.of(
            Map.of("nom", "Espèces", "montant", 15200000),
            Map.of("nom", "Mobile Money", "montant", 10800000),
            Map.of("nom", "Virement Bancaire", "montant", 7500000)
        );
        return ResponseEntity.ok(modes);
    }
    
    // Statistiques parent
    @GetMapping("/dashboard/parent/{parentId}/situation-financiere")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<?> getSituationFinanciere(@PathVariable Long parentId) {
        Map<String, Object> situation = new HashMap<>();
        situation.put("totalDu", 1000000);
        situation.put("totalPaye", 750000);
        situation.put("resteAPayer", 250000);
        return ResponseEntity.ok(situation);
    }
    
    // Méthode supprimée - existe déjà dans EleveController
    // Pour obtenir les enfants d'un parent, utiliser : GET /api/eleves/parent/{parentId}
    
    @GetMapping("/dashboard/parent/{parentId}/performances-enfants")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<?> getPerformancesEnfants(@PathVariable Long parentId) {
        List<Map<String, Object>> performances = List.of(
            Map.of("matiere", "Mathématiques", "elevenom", "Jean Dupont", "note", 16),
            Map.of("matiere", "Français", "elevenom", "Jean Dupont", "note", 15)
        );
        return ResponseEntity.ok(performances);
    }
    
    // Statistiques enseignant
    @GetMapping("/enseignants/{enseignantId}/statistiques")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<?> getStatistiquesEnseignant(@PathVariable Long enseignantId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("nombreClasses", 4);
        stats.put("nombreHeures", 18);
        stats.put("totalEleves", 120);
        stats.put("nombreEleves", 30);
        stats.put("tendance", "↑ 0.5 ce mois");
        stats.put("tauxPresence", 92);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/dashboard/enseignant/{enseignantId}/classes")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<?> getClassesEnseignant(@PathVariable Long enseignantId) {
        List<Map<String, Object>> classes = List.of(
            Map.of("id", 1, "nom", "6ème A", "nombreEle-ves", 30, "moyenne", 14.5),
            Map.of("id", 2, "nom", "5ème B", "nombreEle-ves", 28, "moyenne", 15.2),
            Map.of("id", 4, "nom", "3ème A", "nombreEleves", 30, "moyenne", 14.9)
        );
        return ResponseEntity.ok(classes);
    }
    
    @GetMapping("/enseignants/{enseignantId}/performances")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<?> getPerformancesClasses(@PathVariable Long enseignantId) {
        List<Map<String, Object>> performances = List.of(
            Map.of("classe", "6ème A", "moyenne", 14.5),
            Map.of("classe", "5ème B", "moyenne", 15.2),
            Map.of("classe", "4ème C", "moyenne", 13.8),
            Map.of("classe", "3ème A", "moyenne", 14.9)
        );
        return ResponseEntity.ok(performances);
    }
    
    @GetMapping("/enseignants/{enseignantId}/eleves-difficulte")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<?> getElevesDifficulte(@PathVariable Long enseignantId) {
        List<Map<String, Object>> eleves = List.of(
            Map.of("id", 1, "prenom", "Pierre", "nom", "Ndayizeye", 
                   "classe", "6ème A", "moyenne", 8.5, "motif", "Absences répétées")
        );
        return ResponseEntity.ok(eleves);
    }
    
    @GetMapping("/enseignants/{enseignantId}/prochains-cours")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<?> getProchainsCours(@PathVariable Long enseignantId) {
        List<Map<String, Object>> cours = List.of(
            Map.of("matiere", "Mathématiques", "classe", "6ème A", 
                   "horaire", "08h00 - 10h00", "salle", "101", "jour", "Demain"),
            Map.of("matiere", "Algèbre", "classe", "5ème B", 
                   "horaire", "10h30 - 12h30", "salle", "102", "jour", "Demain")
        );
        return ResponseEntity.ok(cours);
    }
    
    // Annonces
    @GetMapping("/annonces/recentes")
    public ResponseEntity<?> getAnnoncesRecentes(@RequestParam(defaultValue = "5") int limit) {
        List<Map<String, Object>> annonces = List.of(
            Map.of("titre", "Réunion des parents", "datePublication", "2024-01-15"),
            Map.of("titre", "Journée portes ouvertes", "datePublication", "2024-01-10")
        );
        return ResponseEntity.ok(annonces);
    }
    
    // Année scolaire
    @GetMapping("/annees-scolaires/active")
    public ResponseEntity<?> getAnneeScolaireActive() {
        Map<String, Object> annee = new HashMap<>();
        annee.put("id", 1);
        annee.put("libelle", "2024-2025");
        annee.put("dateDebut", "2024-09-01");
        annee.put("dateFin", "2025-06-30");
        return ResponseEntity.ok(annee);
    }
}
