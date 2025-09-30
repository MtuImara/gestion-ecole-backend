package com.gescom.ecole.entity.finance;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypeReduction;
import com.gescom.ecole.entity.scolaire.Eleve;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reductions",
    uniqueConstraints = @UniqueConstraint(columnNames = "code")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reduction extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "designation", nullable = false, length = 150)
    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_reduction", nullable = false, length = 30)
    private TypeReduction typeReduction;

    @Column(name = "pourcentage", precision = 5, scale = 2)
    private BigDecimal pourcentage;

    @Column(name = "montant_fixe", precision = 10, scale = 2)
    private BigDecimal montantFixe;

    @Column(name = "conditions", columnDefinition = "TEXT")
    private String conditions;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "cumulable", nullable = false)
    private Boolean cumulable = false;

    @ManyToMany
    @JoinTable(
        name = "reduction_eleves",
        joinColumns = @JoinColumn(name = "reduction_id"),
        inverseJoinColumns = @JoinColumn(name = "eleve_id")
    )
    private Set<Eleve> eleves = new HashSet<>();

    @ManyToMany(mappedBy = "reductions")
    private Set<Facture> factures = new HashSet<>();

    public BigDecimal appliquer(BigDecimal montantBase) {
        if (pourcentage != null) {
            return montantBase.multiply(pourcentage).divide(BigDecimal.valueOf(100));
        } else if (montantFixe != null) {
            return montantFixe;
        }
        return BigDecimal.ZERO;
    }

    public boolean verifierEligibilite(Eleve eleve) {
        LocalDate aujourd = LocalDate.now();
        
        // Vérifier les dates de validité
        if (dateDebut != null && aujourd.isBefore(dateDebut)) {
            return false;
        }
        if (dateFin != null && aujourd.isAfter(dateFin)) {
            return false;
        }
        
        // Vérifier si la réduction est active
        if (!active) {
            return false;
        }
        
        // Vérifier si l'élève est éligible
        if (!eleves.isEmpty() && !eleves.contains(eleve)) {
            return false;
        }
        
        return true;
    }

    public BigDecimal calculerMontant(BigDecimal montantBase) {
        return appliquer(montantBase);
    }
}
