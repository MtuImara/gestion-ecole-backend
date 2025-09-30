package com.gescom.ecole.dto.reporting;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueDTO {
    
    private Long id;
    private String typeStatistique;
    private String categorie;
    private String periode;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal valeurNumerique;
    private Long valeurEntiere;
    private BigDecimal valeurPourcentage;
    private String valeurTexte;
    private Map<String, Object> donneesComplexes;
    private LocalDateTime dateCalcul;
    private String entiteLieeType;
    private Long entiteLieeId;
    private Long anneeScolaireId;
    private Long periodeId;
    
    // Métadonnées
    private String unite;
    private String format;
    private String description;
    private Boolean estValide;
    
    // Comparaisons
    private BigDecimal valeurPrecedente;
    private BigDecimal variation;
    private String tendance;
    
    // Graphique
    private String typeGraphique;
    private Map<String, Object> optionsGraphique;
}
