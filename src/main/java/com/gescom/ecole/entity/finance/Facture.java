package com.gescom.ecole.entity.finance;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.StatutFacture;
import com.gescom.ecole.common.enums.TypeFacture;
import com.gescom.ecole.entity.scolaire.Eleve;
import com.gescom.ecole.entity.scolaire.AnneeScolaire;
import com.gescom.ecole.entity.scolaire.Periode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factures",
    uniqueConstraints = @UniqueConstraint(columnNames = "numero_facture"),
    indexes = {
        @Index(name = "idx_facture_numero", columnList = "numero_facture"),
        @Index(name = "idx_facture_eleve", columnList = "eleve_id"),
        @Index(name = "idx_facture_statut", columnList = "statut")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture extends BaseEntity {

    @Column(name = "numero_facture", nullable = false, unique = true, length = 50)
    private String numeroFacture;

    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance;

    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "montant_paye", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column(name = "montant_restant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRestant;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 30)
    private StatutFacture statut = StatutFacture.BROUILLON;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_facture", nullable = false, length = 30)
    private TypeFacture typeFacture;

    @Column(name = "devise", nullable = false, length = 10)
    private String devise = "XAF";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rappel_envoye", nullable = false)
    private Boolean rappelEnvoye = false;

    @Column(name = "nombre_rappels")
    private Integer nombreRappels = 0;

    @Column(name = "dernier_rappel")
    private LocalDate dernierRappel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_scolaire_id", nullable = false)
    private AnneeScolaire anneeScolaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periode_id")
    private Periode periode;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneFacture> lignes = new ArrayList<>();

    @OneToMany(mappedBy = "facture")
    private List<Paiement> paiements = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "facture_derogations",
        joinColumns = @JoinColumn(name = "facture_id"),
        inverseJoinColumns = @JoinColumn(name = "derogation_id")
    )
    private List<Derogation> derogations = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "facture_reductions",
        joinColumns = @JoinColumn(name = "facture_id"),
        inverseJoinColumns = @JoinColumn(name = "reduction_id")
    )
    private List<Reduction> reductions = new ArrayList<>();

    @ManyToMany(mappedBy = "facturesCouvertes")
    private List<Bourse> bourses = new ArrayList<>();

    public void calculerSolde() {
        this.montantRestant = this.montantTotal.subtract(this.montantPaye);
        updateStatut();
    }

    private void updateStatut() {
        if (montantPaye.compareTo(BigDecimal.ZERO) == 0) {
            if (LocalDate.now().isAfter(dateEcheance)) {
                this.statut = StatutFacture.EN_RETARD;
            } else {
                this.statut = StatutFacture.EMISE;
            }
        } else if (montantPaye.compareTo(montantTotal) < 0) {
            this.statut = StatutFacture.PARTIELLEMENT_PAYEE;
        } else {
            this.statut = StatutFacture.PAYEE;
        }
    }

    public boolean estEchue() {
        return LocalDate.now().isAfter(dateEcheance) && montantRestant.compareTo(BigDecimal.ZERO) > 0;
    }

    public void envoyerRappel() {
        this.rappelEnvoye = true;
        this.nombreRappels++;
        this.dernierRappel = LocalDate.now();
    }

    public String genererNumeroFacture() {
        String prefix = "FAC";
        String annee = String.valueOf(LocalDate.now().getYear());
        String sequence = String.format("%06d", getId() != null ? getId() : 1);
        this.numeroFacture = prefix + "-" + annee + "-" + sequence;
        return this.numeroFacture;
    }
    
    // Getters et Setters manuels pour contourner le problème Lombok
    public Long getId() {
        // Cette méthode devrait être héritée de BaseEntity
        return null;
    }
    
    public String getNumeroFacture() {
        return numeroFacture;
    }
    
    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }
    
    public BigDecimal getMontantTotal() {
        return montantTotal;
    }
    
    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }
    
    public BigDecimal getMontantPaye() {
        return montantPaye;
    }
    
    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }
    
    public BigDecimal getMontantRestant() {
        return montantRestant;
    }
    
    public void setMontantRestant(BigDecimal montantRestant) {
        this.montantRestant = montantRestant;
    }
    
    public StatutFacture getStatut() {
        return statut;
    }
    
    public void setStatut(StatutFacture statut) {
        this.statut = statut;
    }
}
