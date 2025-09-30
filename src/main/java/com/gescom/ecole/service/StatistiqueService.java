package com.gescom.ecole.service;

import com.gescom.ecole.dto.reporting.DashboardStatsDTO;
import com.gescom.ecole.dto.reporting.StatistiqueDTO;
import com.gescom.ecole.dto.reporting.StatistiqueFinanciereDTO;
import com.gescom.ecole.dto.reporting.StatistiqueScolaireDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatistiqueService {
    
    // Statistiques générales du dashboard
    DashboardStatsDTO getDashboardStats();
    DashboardStatsDTO getDashboardStatsByAnneeScolaire(Long anneeScolaireId);
    
    // Statistiques scolaires
    StatistiqueScolaireDTO getStatistiquesScolaires(Long anneeScolaireId);
    Map<String, Object> getTauxReussiteParClasse(Long anneeScolaireId, Long periodeId);
    Map<String, Object> getTauxPresenceParClasse(LocalDate debut, LocalDate fin);
    Map<String, Object> getEvolutionEffectifs(Long anneeScolaireId);
    List<Map<String, Object>> getTop10Eleves(Long classeId, Long periodeId);
    Map<String, Object> getDistributionNotes(Long classeId, Long matiereId, Long periodeId);
    
    // Statistiques financières
    StatistiqueFinanciereDTO getStatistiquesFinancieres(Long anneeScolaireId);
    Map<String, Object> getEvolutionRecettes(LocalDate debut, LocalDate fin);
    Map<String, Object> getTauxRecouvrement(Long anneeScolaireId);
    List<Map<String, Object>> getImpayes(Long anneeScolaireId);
    Map<String, Object> getRepartitionPaiementsParMode(LocalDate debut, LocalDate fin);
    Map<String, Object> getAnalyseBourses(Long anneeScolaireId);
    
    // Statistiques par entité
    Map<String, Object> getStatistiquesEleve(Long eleveId);
    Map<String, Object> getStatistiquesClasse(Long classeId);
    Map<String, Object> getStatistiquesEnseignant(Long enseignantId);
    Map<String, Object> getStatistiquesParent(Long parentId);
    
    // Statistiques de présence
    Map<String, Object> getTauxAbsenteisme(Long classeId, LocalDate debut, LocalDate fin);
    Map<String, Object> getAbsencesParMotif(LocalDate debut, LocalDate fin);
    List<Map<String, Object>> getElevesAbsentistes(Long classeId, Integer seuilAbsences);
    
    // Statistiques disciplinaires
    Map<String, Object> getSanctionsParType(Long anneeScolaireId);
    Map<String, Object> getEvolutionDiscipline(Long classeId, LocalDate debut, LocalDate fin);
    
    // Statistiques personnalisées
    StatistiqueDTO calculerStatistique(String type, Map<String, Object> parametres);
    List<StatistiqueDTO> getStatistiquesParPeriode(String type, String periode, LocalDate debut, LocalDate fin);
    
    // Export
    byte[] exportStatistiques(String type, String format, Map<String, Object> parametres);
    
    // Cache et performance
    void rafraichirStatistiques();
    void calculerStatistiquesJournalieres();
    void nettoyerStatistiquesAnciennes(Integer joursConservation);
}
