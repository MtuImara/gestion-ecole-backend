package com.gescom.ecole.entity.finance;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypeBourse;
import com.gescom.ecole.common.enums.StatutBourse;
import com.gescom.ecole.entity.scolaire.Eleve;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bourses",
    uniqueConstraints = @UniqueConstraint(columnNames = "numero_bourse")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bourse extends BaseEntity {

    @Column(name = "numero_bourse", nullable = false, unique = true, length = 50)
    private String numeroBourse;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_bourse", nullable = false, length = 30)
    private TypeBourse typeBourse;

    @Column(name = "organisme", nullable = false, length = 150)
    private String organisme;

    @Column(name = "montant", precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "pourcentage_couverture", precision = 5, scale = 2)
    private BigDecimal pourcentageCouverture;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutBourse statut = StatutBourse.EN_ATTENTE;

    @Column(name = "conditions", columnDefinition = "TEXT")
    private String conditions;

    @Column(name = "documents_requis", columnDefinition = "TEXT")
    private String documentsRequis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    @ManyToMany
    @JoinTable(
        name = "bourse_factures",
        joinColumns = @JoinColumn(name = "bourse_id"),
        inverseJoinColumns = @JoinColumn(name = "facture_id")
    )
    private Set<Facture> facturesCouvertes = new HashSet<>();

    public void attribuer() {
        this.statut = StatutBourse.ACTIVE;
        genererNumeroBourse();
    }

    public void renouveler(LocalDate nouvelleDateFin) {
        if (StatutBourse.ACTIVE.equals(this.statut)) {
            this.dateFin = nouvelleDateFin;
        }
    }

    public void suspendre() {
        this.statut = StatutBourse.SUSPENDUE;
    }

    public void terminer() {
        this.statut = StatutBourse.TERMINEE;
    }

    public BigDecimal calculerCouverture(BigDecimal montantFacture) {
        if (pourcentageCouverture != null) {
            return montantFacture.multiply(pourcentageCouverture).divide(BigDecimal.valueOf(100));
        } else if (montant != null) {
            return montant.min(montantFacture);
        }
        return BigDecimal.ZERO;
    }

    private String genererNumeroBourse() {
        String prefix = "BRS";
        String annee = String.valueOf(LocalDate.now().getYear());
        String sequence = String.format("%06d", getId() != null ? getId() : 1);
        this.numeroBourse = prefix + "-" + annee + "-" + sequence;
        return this.numeroBourse;
    }

    public boolean estActive() {
        LocalDate aujourd = LocalDate.now();
        return StatutBourse.ACTIVE.equals(statut) 
            && !aujourd.isBefore(dateDebut) 
            && !aujourd.isAfter(dateFin);
    }
}
