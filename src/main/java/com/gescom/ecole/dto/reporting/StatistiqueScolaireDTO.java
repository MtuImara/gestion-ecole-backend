package com.gescom.ecole.dto.reporting;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueScolaireDTO {
    
    // Effectifs
    private Long totalEleves;
    private Long totalGarcons;
    private Long totalFilles;
    private Map<String, Long> effectifsParNiveau;
    private Map<String, Long> effectifsParClasse;
    private BigDecimal ratioGarconsFilles;
    private BigDecimal tauxRemplissageClasses;
    
    // Performance académique
    private BigDecimal moyenneGeneraleEcole;
    private Map<String, BigDecimal> moyenneParNiveau;
    private Map<String, BigDecimal> moyenneParClasse;
    private Map<String, BigDecimal> moyenneParMatiere;
    private BigDecimal tauxReussiteGlobal;
    private Map<String, BigDecimal> tauxReussiteParNiveau;
    private Long nombreElevesExcellents; // >= 16/20
    private Long nombreElevesBien; // >= 14/20
    private Long nombreElevesMoyen; // >= 10/20
    private Long nombreElevesFaible; // < 10/20
    
    // Progression
    private BigDecimal progressionMoyenne;
    private List<ProgressionDTO> progressionParMois;
    private Map<String, BigDecimal> progressionParMatiere;
    
    // Assiduité
    private BigDecimal tauxPresenceGlobal;
    private Map<String, BigDecimal> tauxPresenceParClasse;
    private Long totalAbsences;
    private Long totalRetards;
    private Long absencesJustifiees;
    private Long absencesNonJustifiees;
    private List<EleveAssiduite> elevesAssidus;
    private List<EleveAssiduite> elevesAbsentistes;
    
    // Discipline
    private Long totalSanctions;
    private Map<String, Long> sanctionsParType;
    private Map<String, Long> sanctionsParClasse;
    private List<EleveDiscipline> elevesExemplaires;
    private List<EleveDiscipline> elevesProblematiques;
    
    // Comparaisons
    private ComparisonDTO comparaisonPeriodePrecedente;
    private ComparisonDTO comparaisonAnneePrecedente;
    
    // Prédictions
    private BigDecimal tauxReussitePrevu;
    private Long effectifsPrevus;
    private Map<String, Object> tendances;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressionDTO {
        private String periode;
        private BigDecimal moyenne;
        private BigDecimal variation;
        private String tendance;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EleveAssiduite {
        private Long eleveId;
        private String nom;
        private String prenom;
        private String classe;
        private BigDecimal tauxPresence;
        private Long nombreAbsences;
        private Long nombreRetards;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EleveDiscipline {
        private Long eleveId;
        private String nom;
        private String prenom;
        private String classe;
        private Long nombreSanctions;
        private String derniereSanction;
        private String comportement;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparisonDTO {
        private String periode;
        private Map<String, BigDecimal> valeursPrecedentes;
        private Map<String, BigDecimal> valeursActuelles;
        private Map<String, BigDecimal> variations;
        private Map<String, String> tendances;
    }
}
