package com.gescom.ecole.controller;

import com.gescom.ecole.repository.scolaire.EleveRepository;
import com.gescom.ecole.repository.scolaire.ClasseRepository;
import com.gescom.ecole.repository.finance.FactureRepository;
import com.gescom.ecole.repository.finance.PaiementRepository;
import com.gescom.ecole.repository.utilisateur.UtilisateurRepository;
import com.gescom.ecole.common.enums.StatutEleve;
import com.gescom.ecole.common.enums.ModePaiement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Tableau de bord et statistiques")
public class DashboardController {

    private final EleveRepository eleveRepository;
    private final ClasseRepository classeRepository;
    private final FactureRepository factureRepository;
    private final PaiementRepository paiementRepository;
    private final UtilisateurRepository utilisateurRepository;
    
    // Constructeur manuel pour remplacer @RequiredArgsConstructor
    public DashboardController(
            EleveRepository eleveRepository,
            ClasseRepository classeRepository,
            FactureRepository factureRepository,
            PaiementRepository paiementRepository,
            UtilisateurRepository utilisateurRepository) {
        this.eleveRepository = eleveRepository;
        this.classeRepository = classeRepository;
        this.factureRepository = factureRepository;
        this.paiementRepository = paiementRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/stats")
    @Operation(summary = "Statistiques générales", description = "Récupère les statistiques générales du système")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'SECRETAIRE')")
    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques élèves
        stats.put("totalEleves", eleveRepository.count());
        stats.put("elevesActifs", eleveRepository.countByStatut(StatutEleve.ACTIF));
        stats.put("elevesSuspendus", eleveRepository.countByStatut(StatutEleve.SUSPENDU));
        stats.put("elevesDiplomes", eleveRepository.countByStatut(StatutEleve.DIPLOME));
        
        // Statistiques classes
        stats.put("totalClasses", classeRepository.count());
        stats.put("classesActives", classeRepository.findActiveClasses().size());
        
        // Statistiques utilisateurs
        stats.put("totalUtilisateurs", utilisateurRepository.count());
        stats.put("utilisateursActifs", utilisateurRepository.countActiveUsers());
        
        return stats;
    }

    @GetMapping("/finance")
    @Operation(summary = "Statistiques financières", description = "Récupère les statistiques financières")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE')")
    public Map<String, Object> getStatistiquesFinancieres() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);
        
        // Factures
        stats.put("totalFactures", factureRepository.count());
        stats.put("facturesEchues", factureRepository.findFacturesEchues(today).size());
        
        // Paiements du mois
        BigDecimal totalPaiementsMois = paiementRepository.getTotalPaiementsByPeriode(startOfMonth, endOfMonth);
        stats.put("totalPaiementsMois", totalPaiementsMois != null ? totalPaiementsMois : BigDecimal.ZERO);
        
        // Montants par mode de paiement
        Map<String, Long> paiementsParMode = new HashMap<>();
        paiementsParMode.put("especes", paiementRepository.countByModePaiementAndValide(ModePaiement.ESPECES));
        paiementsParMode.put("virement", paiementRepository.countByModePaiementAndValide(ModePaiement.VIREMENT));
        paiementsParMode.put("cheque", paiementRepository.countByModePaiementAndValide(ModePaiement.CHEQUE));
        paiementsParMode.put("mobile", paiementRepository.countByModePaiementAndValide(ModePaiement.MOBILE_MONEY));
        stats.put("paiementsParMode", paiementsParMode);
        
        return stats;
    }

    @GetMapping("/recent-activities")
    @Operation(summary = "Activités récentes", description = "Récupère les activités récentes du système")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'SECRETAIRE')")
    public Map<String, Object> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();
        
        // Derniers élèves inscrits (5 derniers)
        activities.put("derniersEleves", eleveRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateInscription"))
        ).getContent());
        
        // Derniers paiements (5 derniers)
        activities.put("derniersPaiements", paiementRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "datePaiement"))
        ).getContent());
        
        // Dernières factures (5 dernières)
        activities.put("dernieresFactures", factureRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateEmission"))
        ).getContent());
        
        return activities;
    }

    @GetMapping("/summary")
    @Operation(summary = "Résumé du tableau de bord", description = "Récupère un résumé complet pour le tableau de bord")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPTABLE', 'SECRETAIRE')")
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Combiner toutes les statistiques
        summary.put("general", getStatistiques());
        summary.put("finance", getStatistiquesFinancieres());
        summary.put("activities", getRecentActivities());
        
        // Ajouter la date du jour
        summary.put("date", LocalDate.now());
        
        return summary;
    }
}
