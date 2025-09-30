package com.gescom.ecole.entity.finance;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypeFrais;
import com.gescom.ecole.common.enums.FrequencePaiement;
import com.gescom.ecole.entity.scolaire.AnneeScolaire;
import com.gescom.ecole.entity.scolaire.Niveau;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "configuration_frais",
    uniqueConstraints = @UniqueConstraint(columnNames = {"code", "annee_scolaire_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationFrais extends BaseEntity {

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "designation", nullable = false, length = 150)
    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_frais", nullable = false, length = 30)
    private TypeFrais typeFrais;

    @Column(name = "montant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "devise", nullable = false, length = 10)
    private String devise = "XAF";

    @Enumerated(EnumType.STRING)
    @Column(name = "frequence", nullable = false, length = 20)
    private FrequencePaiement frequence;

    @Column(name = "obligatoire", nullable = false)
    private Boolean obligatoire = true;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_scolaire_id", nullable = false)
    private AnneeScolaire anneeScolaire;

    @ManyToMany
    @JoinTable(
        name = "configuration_frais_niveaux",
        joinColumns = @JoinColumn(name = "configuration_frais_id"),
        inverseJoinColumns = @JoinColumn(name = "niveau_id")
    )
    private Set<Niveau> niveaux = new HashSet<>();

    @OneToMany(mappedBy = "configurationFrais")
    private Set<LigneFacture> lignesFacture = new HashSet<>();

    public BigDecimal appliquerReduction(BigDecimal pourcentageReduction) {
        BigDecimal reduction = montant.multiply(pourcentageReduction).divide(BigDecimal.valueOf(100));
        return montant.subtract(reduction);
    }

    public BigDecimal calculerMontantTotal(Integer nombrePeriodes) {
        if (FrequencePaiement.UNIQUE.equals(frequence)) {
            return montant;
        }
        return montant.multiply(BigDecimal.valueOf(nombrePeriodes));
    }
}
