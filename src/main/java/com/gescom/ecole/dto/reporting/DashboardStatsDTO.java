package com.gescom.ecole.dto.reporting;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    
    // Statistiques générales
    private Long totalEleves;
    private Long totalEnseignants;
    private Long totalParents;
    private Long totalClasses;
    private Long elevesActifs;
    private Long elevesInactifs;
    private Long nouveauxEleves;
    
    // Statistiques financières
    private BigDecimal montantTotalFacture;
    private BigDecimal montantTotalPaye;
    private BigDecimal montantTotalImpaye;
    private BigDecimal tauxRecouvrement;
    private Long nombreFacturesEmises;
    private Long nombreFacturesPayees;
    private Long nombreFacturesImpayees;
    
    // Statistiques de présence (aujourd'hui)
    private Long elevesPresents;
    private Long elevesAbsents;
    private Long elevesRetard;
    private BigDecimal tauxPresence;
    
    // Statistiques académiques
    private BigDecimal moyenneGenerale;
    private BigDecimal tauxReussite;
    private Long nombreEvaluations;
    private Long prochainExamen;
    
    // Statistiques de communication
    private Long messagesNonLus;
    private Long notificationsNonLues;
    private Long annoncesActives;
    
    // Graphiques
    private List<ChartDataDTO> evolutionEffectifs;
    private List<ChartDataDTO> evolutionRecettes;
    private List<ChartDataDTO> repartitionParNiveau;
    private List<ChartDataDTO> performanceParClasse;
    
    // Top lists
    private List<TopItemDTO> top5Classes;
    private List<TopItemDTO> top5Eleves;
    private List<TopItemDTO> derniersPayements;
    private List<TopItemDTO> prochainsEvenements;
    
    // Alertes
    private List<AlerteDTO> alertes;
    
    // Métadonnées
    private LocalDateTime dateGeneration;
    private String periodeAffichee;
    private Long anneeScolaireId;
    private String anneeScolaireLibelle;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartDataDTO {
        private String label;
        private Object value;
        private String color;
        private Map<String, Object> metadata;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopItemDTO {
        private Long id;
        private String label;
        private String description;
        private Object value;
        private String trend; // UP, DOWN, STABLE
        private BigDecimal variation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlerteDTO {
        private String type; // WARNING, DANGER, INFO, SUCCESS
        private String titre;
        private String message;
        private String action;
        private LocalDateTime date;
    }
}
