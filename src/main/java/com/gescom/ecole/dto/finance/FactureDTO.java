package com.gescom.ecole.dto.finance;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.gescom.ecole.common.enums.StatutFacture;
import com.gescom.ecole.common.enums.TypeFacture;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactureDTO {
    
    private Long id;
    private String numeroFacture;
    
    @NotNull(message = "La date d'émission est obligatoire")
    private LocalDate dateEmission;
    
    @NotNull(message = "La date d'échéance est obligatoire")
    private LocalDate dateEcheance;
    
    @NotNull(message = "Le montant total est obligatoire")
    @Positive(message = "Le montant total doit être positif")
    private BigDecimal montantTotal;
    
    private BigDecimal montantPaye;
    private BigDecimal montantRestant;
    
    @NotNull(message = "Le statut est obligatoire")
    private StatutFacture statut;
    
    @NotNull(message = "Le type de facture est obligatoire")
    private TypeFacture typeFacture;
    
    private String devise;
    private String description;
    private Boolean rappelEnvoye;
    private Integer nombreRappels;
    private LocalDate dernierRappel;
    
    // Relations
    @NotNull(message = "L'élève est obligatoire")
    private Long eleve;
    private EleveSimpleDTO eleveInfo;
    
    @NotNull(message = "L'année scolaire est obligatoire")
    private Long anneeScolaire;
    private String anneeScolaireDesignation;
    
    private Long periode;
    private String periodeDesignation;
    
    // Lignes de facture
    private List<LigneFactureDTO> lignes;
    
    // Paiements
    private List<PaiementSimpleDTO> paiements;
    
    @JsonSetter("statut")
    public void setStatutFromString(String statut) {
        if (statut != null) {
            try {
                this.statut = StatutFacture.valueOf(statut.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.statut = StatutFacture.BROUILLON;
            }
        }
    }
    
    @JsonSetter("typeFacture")
    public void setTypeFactureFromString(String typeFacture) {
        if (typeFacture != null) {
            try {
                this.typeFacture = TypeFacture.valueOf(typeFacture.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.typeFacture = null;
            }
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EleveSimpleDTO {
        private Long id;
        private String matricule;
        private String nom;
        private String prenom;
        private String classe;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaiementSimpleDTO {
        private Long id;
        private String numeroPaiement;
        private LocalDate datePaiement;
        private BigDecimal montant;
        private String modePaiement;
        private String statut;
    }
}
