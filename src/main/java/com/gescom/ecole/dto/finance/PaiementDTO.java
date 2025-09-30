package com.gescom.ecole.dto.finance;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.gescom.ecole.common.enums.ModePaiement;
import com.gescom.ecole.common.enums.StatutPaiement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementDTO {
    
    private Long id;
    private String numeroPaiement;
    
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;
    
    private LocalDateTime datePaiement;
    
    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;
    
    private StatutPaiement statut;
    
    private String numeroTransaction;
    private String referenceExterne;
    private String banque;
    private String numeroCheque;
    private String description;
    private LocalDateTime dateValidation;
    
    // Relations
    @NotNull(message = "La facture est obligatoire")
    private Long facture;
    private FactureSimpleDTO factureInfo;
    
    private Long parent;
    private ParentSimpleDTO parentInfo;
    
    private Long recu;
    
    @JsonSetter("modePaiement")
    public void setModePaiementFromString(String modePaiement) {
        if (modePaiement != null) {
            try {
                this.modePaiement = ModePaiement.valueOf(modePaiement.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.modePaiement = null;
            }
        }
    }
    
    @JsonSetter("statut")
    public void setStatutFromString(String statut) {
        if (statut != null) {
            try {
                this.statut = StatutPaiement.valueOf(statut.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.statut = StatutPaiement.EN_ATTENTE;
            }
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FactureSimpleDTO {
        private Long id;
        private String numeroFacture;
        private BigDecimal montantTotal;
        private BigDecimal montantRestant;
        private String eleveNom;
        private String elevePrenom;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParentSimpleDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String telephone;
        private String email;
    }
    
    // Getters et Setters manuels
    public String getNumeroPaiement() {
        return numeroPaiement;
    }
    
    public void setNumeroPaiement(String numeroPaiement) {
        this.numeroPaiement = numeroPaiement;
    }
    
    public BigDecimal getMontant() {
        return montant;
    }
    
    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    
    public Long getFacture() {
        return facture;
    }
    
    public void setFacture(Long facture) {
        this.facture = facture;
    }
    
    public Long getParent() {
        return parent;
    }
    
    public void setParent(Long parent) {
        this.parent = parent;
    }
}
