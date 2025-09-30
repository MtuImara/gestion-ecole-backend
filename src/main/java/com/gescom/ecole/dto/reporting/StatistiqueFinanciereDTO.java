package com.gescom.ecole.dto.reporting;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueFinanciereDTO {
    
    // Revenus
    private BigDecimal recettesTotales;
    private BigDecimal recettesFraisScolarite;
    private BigDecimal recettesFraisInscription;
    private BigDecimal recettesAutres;
    private Map<String, BigDecimal> recettesParMois;
    private Map<String, BigDecimal> recettesParTypeFacture;
    private Map<String, BigDecimal> recettesParNiveau;
    private Map<String, BigDecimal> recettesParClasse;
    
    // Facturation
    private Long nombreFacturesEmises;
    private BigDecimal montantTotalFacture;
    private BigDecimal montantMoyenFacture;
    private Map<String, Long> nombreFacturesParStatut;
    private Map<String, BigDecimal> montantFacturesParStatut;
    
    // Paiements
    private Long nombrePaiements;
    private BigDecimal montantTotalPaye;
    private BigDecimal montantMoyenPaiement;
    private Map<String, Long> paiementsParMode;
    private Map<String, BigDecimal> montantParModePaiement;
    private List<PaiementTendance> evolutionPaiements;
    
    // Impayés
    private BigDecimal montantTotalImpaye;
    private Long nombreFacturesImpayees;
    private BigDecimal tauxRecouvrement;
    private List<ImpayeDTO> top10Impayes;
    private Map<String, BigDecimal> impayesParClasse;
    private Map<String, Long> ancienneteImpayes; // 0-30j, 31-60j, 61-90j, >90j
    
    // Bourses et réductions
    private Long nombreBoursiers;
    private BigDecimal montantTotalBourses;
    private BigDecimal montantTotalReductions;
    private Map<String, Long> boursiersParType;
    private Map<String, BigDecimal> montantBoursesParType;
    
    // Prévisions
    private BigDecimal recettesPrevisionnelles;
    private BigDecimal recettesRealisees;
    private BigDecimal tauxRealisation;
    private Map<String, BigDecimal> previsionsParMois;
    
    // Analyse comparative
    private ComparisonFinanciere comparaisonMoisPrecedent;
    private ComparisonFinanciere comparaisonAnneePrecedente;
    private List<TendanceFinanciere> tendances;
    
    // Indicateurs de performance
    private BigDecimal delaiMoyenPaiement; // en jours
    private BigDecimal tauxFacturesPayeesATemps;
    private BigDecimal coutMoyenParEleve;
    private Map<String, BigDecimal> kpis;
    
    // Alertes financières
    private List<AlerteFinanciere> alertes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaiementTendance {
        private String periode;
        private BigDecimal montant;
        private Long nombre;
        private BigDecimal variation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImpayeDTO {
        private Long eleveId;
        private String nomEleve;
        private String classe;
        private BigDecimal montantDu;
        private Long joursRetard;
        private LocalDate dateEcheance;
        private String statut;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparisonFinanciere {
        private String periode;
        private BigDecimal recettesPrecedentes;
        private BigDecimal recettesActuelles;
        private BigDecimal variationMontant;
        private BigDecimal variationPourcentage;
        private String tendance;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TendanceFinanciere {
        private String indicateur;
        private String tendance; // HAUSSE, BAISSE, STABLE
        private BigDecimal valeurActuelle;
        private BigDecimal valeurPrecedente;
        private BigDecimal variation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlerteFinanciere {
        private String type; // IMPAYE, RETARD, ANOMALIE
        private String niveau; // INFO, WARNING, DANGER
        private String message;
        private BigDecimal montantConcerne;
        private Long nombreConcerne;
        private LocalDate date;
    }
}
