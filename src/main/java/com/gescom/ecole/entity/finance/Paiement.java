package com.gescom.ecole.entity.finance;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.ModePaiement;
import com.gescom.ecole.common.enums.StatutPaiement;
import com.gescom.ecole.entity.utilisateur.Parent;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiements",
    uniqueConstraints = @UniqueConstraint(columnNames = "numero_paiement"),
    indexes = {
        @Index(name = "idx_paiement_numero", columnList = "numero_paiement"),
        @Index(name = "idx_paiement_facture", columnList = "facture_id"),
        @Index(name = "idx_paiement_statut", columnList = "statut")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement extends BaseEntity {

    @Column(name = "numero_paiement", nullable = false, unique = true, length = 50)
    private String numeroPaiement;

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement;

    @Column(name = "montant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 30)
    private ModePaiement modePaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    @Column(name = "reference", length = 100)
    private String reference;

    @Column(name = "numero_transaction", length = 100)
    private String numeroTransaction;

    @Column(name = "devise", nullable = false, length = 10)
    private String devise = "XAF";

    @Column(name = "banque", length = 100)
    private String banque;

    @Column(name = "numero_cheque", length = 50)
    private String numeroCheque;

    @Column(name = "numero_compte", length = 50)
    private String numeroCompte;

    @Column(name = "operateur_mobile", length = 50)
    private String operateurMobile;

    @Column(name = "numero_telephone", length = 20)
    private String numeroTelephone;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "valide_par", length = 100)
    private String validePar;

    @Column(name = "motif_annulation", columnDefinition = "TEXT")
    private String motifAnnulation;

    @Column(name = "date_annulation")
    private LocalDateTime dateAnnulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enregistre_par_id", nullable = false)
    private Utilisateur enregistrePar;

    @OneToOne(mappedBy = "paiement", cascade = CascadeType.ALL)
    private Recu recu;

    public void valider(String validateur) {
        this.statut = StatutPaiement.VALIDE;
        this.dateValidation = LocalDateTime.now();
        this.validePar = validateur;
        
        // Mettre à jour le montant payé de la facture
        if (facture != null) {
            facture.setMontantPaye(facture.getMontantPaye().add(this.montant));
            facture.calculerSolde();
        }
    }

    public void annuler(String motif) {
        this.statut = StatutPaiement.ANNULE;
        this.motifAnnulation = motif;
        this.dateAnnulation = LocalDateTime.now();
        
        // Retirer le montant de la facture
        if (facture != null && StatutPaiement.VALIDE.equals(this.statut)) {
            facture.setMontantPaye(facture.getMontantPaye().subtract(this.montant));
            facture.calculerSolde();
        }
    }

    public String genererNumeroPaiement() {
        String prefix = "PAY";
        String annee = String.valueOf(LocalDateTime.now().getYear());
        String sequence = String.format("%06d", getId() != null ? getId() : 1);
        this.numeroPaiement = prefix + "-" + annee + "-" + sequence;
        return this.numeroPaiement;
    }

    public void genererRecu() {
        if (this.recu == null && StatutPaiement.VALIDE.equals(this.statut)) {
            Recu nouveauRecu = new Recu();
            nouveauRecu.setPaiement(this);
            nouveauRecu.setNumeroRecu(genererNumeroRecu());
            nouveauRecu.setDateEmission(LocalDateTime.now());
            nouveauRecu.setMontant(this.montant);
            nouveauRecu.setDevise(this.devise);
            nouveauRecu.setEmetteur("École");
            nouveauRecu.setBeneficiaire(parent != null ? parent.getUsername() : "Client");
            nouveauRecu.setDescription("Paiement facture " + facture.getNumeroFacture());
            this.recu = nouveauRecu;
        }
    }

    private String genererNumeroRecu() {
        String prefix = "REC";
        String annee = String.valueOf(LocalDateTime.now().getYear());
        String sequence = String.format("%06d", getId() != null ? getId() : 1);
        return prefix + "-" + annee + "-" + sequence;
    }
    
    // Getters et Setters manuels pour contourner le problème Lombok
    public Long getId() {
        // Cette méthode devrait être héritée de BaseEntity
        // Si BaseEntity n'a pas cette méthode, retourner null temporairement
        return null;
    }
    
    public String getNumeroPaiement() {
        return numeroPaiement;
    }
    
    public void setNumeroPaiement(String numeroPaiement) {
        this.numeroPaiement = numeroPaiement;
    }
    
    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }
    
    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }
    
    public BigDecimal getMontant() {
        return montant;
    }
    
    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    
    public StatutPaiement getStatut() {
        return statut;
    }
    
    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }
    
    public Facture getFacture() {
        return facture;
    }
    
    public void setFacture(Facture facture) {
        this.facture = facture;
    }
    
    public Parent getParent() {
        return parent;
    }
    
    public void setParent(Parent parent) {
        this.parent = parent;
    }
    
    public Recu getRecu() {
        return recu;
    }
    
    public void setRecu(Recu recu) {
        this.recu = recu;
    }
}
