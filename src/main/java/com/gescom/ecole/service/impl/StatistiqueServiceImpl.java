package com.gescom.ecole.service.impl;

import com.gescom.ecole.dto.reporting.DashboardStatsDTO;
import com.gescom.ecole.dto.reporting.StatistiqueDTO;
import com.gescom.ecole.dto.reporting.StatistiqueFinanciereDTO;
import com.gescom.ecole.dto.reporting.StatistiqueScolaireDTO;
import com.gescom.ecole.entity.reporting.Statistique;
import com.gescom.ecole.repository.finance.FactureRepository;
import com.gescom.ecole.repository.finance.PaiementRepository;
import com.gescom.ecole.repository.reporting.StatistiqueRepository;
import com.gescom.ecole.repository.scolaire.ClasseRepository;
import com.gescom.ecole.repository.scolaire.EleveRepository;
import com.gescom.ecole.repository.utilisateur.UtilisateurRepository;
import com.gescom.ecole.service.StatistiqueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StatistiqueServiceImpl implements StatistiqueService {

    private final StatistiqueRepository statistiqueRepository;
    private final EleveRepository eleveRepository;
    private final ClasseRepository classeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FactureRepository factureRepository;
    private final PaiementRepository paiementRepository;

    @Override
    @Cacheable(value = "dashboardStats", key = "#root.method.name")
    public DashboardStatsDTO getDashboardStats() {
        log.info("Génération des statistiques du dashboard");
        
        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setTotalEleves(eleveRepository.count());
        dto.setElevesActifs(eleveRepository.countByStatut("ACTIF"));
        dto.setElevesInactifs(eleveRepository.countByStatut("INACTIF"));
        dto.setTotalClasses(classeRepository.count());
        dto.setTotalEnseignants(utilisateurRepository.countByType("ENSEIGNANT"));
        dto.setTotalParents(utilisateurRepository.countByType("PARENT"));
        dto.setMontantTotalFacture(factureRepository.sumMontantTotal());
        dto.setMontantTotalPaye(paiementRepository.sumMontantTotal());
        dto.setMontantTotalImpaye(calculateMontantImpaye());
        dto.setTauxRecouvrement(calculateTauxRecouvrement());
        dto.setNombreFacturesEmises(factureRepository.count());
        dto.setNombreFacturesPayees(factureRepository.countByStatut("PAYEE"));
        dto.setNombreFacturesImpayees(factureRepository.countByStatut("IMPAYEE"));
        dto.setDateGeneration(LocalDateTime.now());
        return dto;
    }

    @Override
    @Cacheable(value = "dashboardStats", key = "#anneeScolaireId")
    public DashboardStatsDTO getDashboardStatsByAnneeScolaire(Long anneeScolaireId) {
        log.info("Génération des statistiques du dashboard pour l'année scolaire {}", anneeScolaireId);
        
        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setAnneeScolaireId(anneeScolaireId);
        dto.setTotalEleves(eleveRepository.countByAnneeScolaireId(anneeScolaireId));
        dto.setElevesActifs(eleveRepository.countByAnneeScolaireIdAndStatut(anneeScolaireId, "ACTIF"));
        dto.setTotalClasses(classeRepository.countByAnneeScolaireId(anneeScolaireId));
        dto.setMontantTotalFacture(factureRepository.sumMontantTotalByAnneeScolaireId(anneeScolaireId));
        dto.setMontantTotalPaye(paiementRepository.sumMontantByAnneeScolaireId(anneeScolaireId));
        dto.setTauxRecouvrement(calculateTauxRecouvrementByAnnee(anneeScolaireId));
        dto.setDateGeneration(LocalDateTime.now());
        return dto;
    }

    @Override
    public StatistiqueScolaireDTO getStatistiquesScolaires(Long anneeScolaireId) {
        log.info("Génération des statistiques scolaires pour l'année {}", anneeScolaireId);
        
        Long totalEleves = eleveRepository.countByAnneeScolaireId(anneeScolaireId);
        Long totalGarcons = eleveRepository.countByAnneeScolaireIdAndGenre(anneeScolaireId, "MASCULIN");
        Long totalFilles = eleveRepository.countByAnneeScolaireIdAndGenre(anneeScolaireId, "FEMININ");
        
        StatistiqueScolaireDTO dto = new StatistiqueScolaireDTO();
        dto.setTotalEleves(totalEleves);
        dto.setTotalGarcons(totalGarcons);
        dto.setTotalFilles(totalFilles);
        dto.setRatioGarconsFilles(calculateRatio(totalGarcons, totalFilles));
        return dto;
    }

    @Override
    public StatistiqueFinanciereDTO getStatistiquesFinancieres(Long anneeScolaireId) {
        log.info("Génération des statistiques financières pour l'année {}", anneeScolaireId);
        
        BigDecimal recettesTotales = paiementRepository.sumMontantByAnneeScolaireId(anneeScolaireId);
        BigDecimal montantTotalFacture = factureRepository.sumMontantTotalByAnneeScolaireId(anneeScolaireId);
        BigDecimal montantTotalPaye = paiementRepository.sumMontantByAnneeScolaireId(anneeScolaireId);
        BigDecimal montantTotalImpaye = montantTotalFacture.subtract(montantTotalPaye);
        
        StatistiqueFinanciereDTO dto = new StatistiqueFinanciereDTO();
        dto.setRecettesTotales(recettesTotales);
        dto.setMontantTotalFacture(montantTotalFacture);
        dto.setMontantTotalPaye(montantTotalPaye);
        dto.setMontantTotalImpaye(montantTotalImpaye);
        dto.setTauxRecouvrement(calculateTauxRecouvrement(montantTotalPaye, montantTotalFacture));
        dto.setNombreFacturesEmises(factureRepository.countByAnneeScolaireId(anneeScolaireId));
        dto.setNombreFacturesImpayees(factureRepository.countByAnneeScolaireIdAndStatut(anneeScolaireId, "IMPAYEE"));
        return dto;
    }

    @Override
    public Map<String, Object> getTauxReussiteParClasse(Long anneeScolaireId, Long periodeId) {
        log.info("Calcul du taux de réussite par classe");
        Map<String, Object> result = new HashMap<>();
        // Implémentation simplifiée - à compléter avec la logique réelle
        result.put("message", "Fonctionnalité à implémenter avec le module de notes");
        return result;
    }

    @Override
    public Map<String, Object> getTauxPresenceParClasse(LocalDate debut, LocalDate fin) {
        log.info("Calcul du taux de présence par classe du {} au {}", debut, fin);
        Map<String, Object> result = new HashMap<>();
        // Implémentation simplifiée - à compléter avec le module d'absences
        result.put("message", "Fonctionnalité à implémenter avec le module d'absences");
        return result;
    }

    @Override
    public Map<String, Object> getEvolutionEffectifs(Long anneeScolaireId) {
        log.info("Calcul de l'évolution des effectifs");
        Map<String, Object> result = new HashMap<>();
        result.put("anneeScolaireId", anneeScolaireId);
        result.put("totalActuel", eleveRepository.countByAnneeScolaireId(anneeScolaireId));
        return result;
    }

    @Override
    public List<Map<String, Object>> getTop10Eleves(Long classeId, Long periodeId) {
        log.info("Récupération du top 10 des élèves");
        List<Map<String, Object>> result = new ArrayList<>();
        // Implémentation simplifiée - à compléter avec le module de notes
        return result;
    }

    @Override
    public Map<String, Object> getDistributionNotes(Long classeId, Long matiereId, Long periodeId) {
        log.info("Calcul de la distribution des notes");
        Map<String, Object> result = new HashMap<>();
        // Implémentation simplifiée - à compléter avec le module de notes
        result.put("message", "Fonctionnalité à implémenter avec le module de notes");
        return result;
    }

    @Override
    public Map<String, Object> getEvolutionRecettes(LocalDate debut, LocalDate fin) {
        log.info("Calcul de l'évolution des recettes du {} au {}", debut, fin);
        Map<String, Object> result = new HashMap<>();
        result.put("periode", String.format("%s - %s", debut, fin));
        result.put("montantTotal", paiementRepository.sumMontantByPeriode(debut, fin));
        return result;
    }

    @Override
    public Map<String, Object> getTauxRecouvrement(Long anneeScolaireId) {
        log.info("Calcul du taux de recouvrement");
        Map<String, Object> result = new HashMap<>();
        BigDecimal taux = calculateTauxRecouvrementByAnnee(anneeScolaireId);
        result.put("tauxRecouvrement", taux);
        result.put("anneeScolaireId", anneeScolaireId);
        return result;
    }

    @Override
    public List<Map<String, Object>> getImpayes(Long anneeScolaireId) {
        log.info("Récupération des impayés");
        List<Map<String, Object>> result = new ArrayList<>();
        // Récupération simplifiée des impayés
        return result;
    }

    @Override
    public Map<String, Object> getRepartitionPaiementsParMode(LocalDate debut, LocalDate fin) {
        log.info("Calcul de la répartition des paiements par mode");
        Map<String, Object> result = new HashMap<>();
        // Implémentation simplifiée
        return result;
    }

    @Override
    public Map<String, Object> getAnalyseBourses(Long anneeScolaireId) {
        log.info("Analyse des bourses");
        Map<String, Object> result = new HashMap<>();
        result.put("nombreBoursiers", eleveRepository.countBoursiers(anneeScolaireId));
        return result;
    }

    @Override
    public Map<String, Object> getStatistiquesEleve(Long eleveId) {
        log.info("Récupération des statistiques de l'élève {}", eleveId);
        Map<String, Object> result = new HashMap<>();
        result.put("eleveId", eleveId);
        // Ajouter plus de statistiques
        return result;
    }

    @Override
    public Map<String, Object> getStatistiquesClasse(Long classeId) {
        log.info("Récupération des statistiques de la classe {}", classeId);
        Map<String, Object> result = new HashMap<>();
        result.put("classeId", classeId);
        result.put("effectif", eleveRepository.countByClasseId(classeId));
        return result;
    }

    @Override
    public Map<String, Object> getStatistiquesEnseignant(Long enseignantId) {
        log.info("Récupération des statistiques de l'enseignant {}", enseignantId);
        Map<String, Object> result = new HashMap<>();
        result.put("enseignantId", enseignantId);
        return result;
    }

    @Override
    public Map<String, Object> getStatistiquesParent(Long parentId) {
        log.info("Récupération des statistiques du parent {}", parentId);
        Map<String, Object> result = new HashMap<>();
        result.put("parentId", parentId);
        return result;
    }

    @Override
    public Map<String, Object> getTauxAbsenteisme(Long classeId, LocalDate debut, LocalDate fin) {
        log.info("Calcul du taux d'absentéisme");
        Map<String, Object> result = new HashMap<>();
        // À implémenter avec le module d'absences
        return result;
    }

    @Override
    public Map<String, Object> getAbsencesParMotif(LocalDate debut, LocalDate fin) {
        log.info("Analyse des absences par motif");
        Map<String, Object> result = new HashMap<>();
        // À implémenter avec le module d'absences
        return result;
    }

    @Override
    public List<Map<String, Object>> getElevesAbsentistes(Long classeId, Integer seuilAbsences) {
        log.info("Récupération des élèves absentéistes");
        List<Map<String, Object>> result = new ArrayList<>();
        // À implémenter avec le module d'absences
        return result;
    }

    @Override
    public Map<String, Object> getSanctionsParType(Long anneeScolaireId) {
        log.info("Analyse des sanctions par type");
        Map<String, Object> result = new HashMap<>();
        // À implémenter avec le module disciplinaire
        return result;
    }

    @Override
    public Map<String, Object> getEvolutionDiscipline(Long classeId, LocalDate debut, LocalDate fin) {
        log.info("Évolution de la discipline");
        Map<String, Object> result = new HashMap<>();
        // À implémenter avec le module disciplinaire
        return result;
    }

    @Override
    public StatistiqueDTO calculerStatistique(String type, Map<String, Object> parametres) {
        log.info("Calcul de statistique personnalisée de type {}", type);
        
        Statistique statistique = new Statistique();
        statistique.setTypeStatistique(type);
        statistique.setCategorie("CUSTOM");
        statistique.setPeriode("PONCTUEL");
        statistique.setDateDebut(LocalDate.now());
        statistique.setDateFin(LocalDate.now());
        statistique.setDateCalcul(LocalDateTime.now());
        
        statistique = statistiqueRepository.save(statistique);
        
        return convertToDTO(statistique);
    }

    @Override
    public List<StatistiqueDTO> getStatistiquesParPeriode(String type, String periode, LocalDate debut, LocalDate fin) {
        log.info("Récupération des statistiques {} pour la période {} du {} au {}", type, periode, debut, fin);
        List<Statistique> statistiques = statistiqueRepository.findStatistiquesParPeriode(debut, fin);
        return statistiques.stream().map(this::convertToDTO).toList();
    }

    @Override
    public byte[] exportStatistiques(String type, String format, Map<String, Object> parametres) {
        log.info("Export des statistiques {} au format {}", type, format);
        // Implémentation simplifiée - à compléter avec une vraie génération PDF/Excel
        return new byte[0];
    }

    @Override
    @Scheduled(cron = "0 0 1 * * ?") // Tous les jours à 1h du matin
    public void rafraichirStatistiques() {
        log.info("Rafraîchissement des statistiques");
        // Recalculer les statistiques en cache
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Tous les jours à 2h du matin
    public void calculerStatistiquesJournalieres() {
        log.info("Calcul des statistiques journalières");
        // Calculer et stocker les statistiques du jour
    }

    @Override
    public void nettoyerStatistiquesAnciennes(Integer joursConservation) {
        log.info("Nettoyage des statistiques de plus de {} jours", joursConservation);
        LocalDateTime dateExpiration = LocalDateTime.now().minusDays(joursConservation);
        int deleted = statistiqueRepository.supprimerStatistiquesAnciennes(dateExpiration);
        log.info("{} statistiques supprimées", deleted);
    }

    // Méthodes utilitaires privées
    private BigDecimal calculateMontantImpaye() {
        BigDecimal total = factureRepository.sumMontantTotal();
        BigDecimal paye = paiementRepository.sumMontantTotal();
        return total != null && paye != null ? total.subtract(paye) : BigDecimal.ZERO;
    }

    private BigDecimal calculateTauxRecouvrement() {
        BigDecimal total = factureRepository.sumMontantTotal();
        BigDecimal paye = paiementRepository.sumMontantTotal();
        return calculateTauxRecouvrement(paye, total);
    }

    private BigDecimal calculateTauxRecouvrementByAnnee(Long anneeScolaireId) {
        BigDecimal total = factureRepository.sumMontantTotalByAnneeScolaireId(anneeScolaireId);
        BigDecimal paye = paiementRepository.sumMontantByAnneeScolaireId(anneeScolaireId);
        return calculateTauxRecouvrement(paye, total);
    }

    private BigDecimal calculateTauxRecouvrement(BigDecimal paye, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (paye == null) {
            return BigDecimal.ZERO;
        }
        return paye.divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateRatio(Long valeur1, Long valeur2) {
        if (valeur2 == null || valeur2 == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(valeur1).divide(BigDecimal.valueOf(valeur2), 2, RoundingMode.HALF_UP);
    }

    private StatistiqueDTO convertToDTO(Statistique statistique) {
        StatistiqueDTO dto = new StatistiqueDTO();
        dto.setId(statistique.getId());
        dto.setTypeStatistique(statistique.getTypeStatistique());
        dto.setCategorie(statistique.getCategorie());
        dto.setPeriode(statistique.getPeriode());
        dto.setDateDebut(statistique.getDateDebut());
        dto.setDateFin(statistique.getDateFin());
        dto.setValeurNumerique(statistique.getValeurNumerique());
        dto.setValeurEntiere(statistique.getValeurEntiere());
        dto.setValeurPourcentage(statistique.getValeurPourcentage());
        dto.setValeurTexte(statistique.getValeurTexte());
        dto.setDateCalcul(statistique.getDateCalcul());
        return dto;
    }
}
