package com.gescom.ecole.entity.finance;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.entity.scolaire.Classe;
import com.gescom.ecole.entity.scolaire.AnneeScolaire;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tarifications_classe",
    uniqueConstraints = @UniqueConstraint(columnNames = {"classe_id", "annee_scolaire_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarificationClasse extends BaseEntity {

    @Column(name = "montant_minerval", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantMinerval;

    @Column(name = "montant_inscription", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantInscription;

    @Column(name = "montant_assurance", precision = 10, scale = 2)
    private BigDecimal montantAssurance = BigDecimal.ZERO;

    @Column(name = "montant_activites", precision = 10, scale = 2)
    private BigDecimal montantActivites = BigDecimal.ZERO;

    @Column(name = "devise", nullable = false, length = 10)
    private String devise = "XAF";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_scolaire_id", nullable = false)
    private AnneeScolaire anneeScolaire;

    public BigDecimal calculerTotal() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(montantMinerval != null ? montantMinerval : BigDecimal.ZERO);
        total = total.add(montantInscription != null ? montantInscription : BigDecimal.ZERO);
        total = total.add(montantAssurance != null ? montantAssurance : BigDecimal.ZERO);
        total = total.add(montantActivites != null ? montantActivites : BigDecimal.ZERO);
        return total;
    }

    public BigDecimal appliquerRemise(BigDecimal pourcentageRemise) {
        BigDecimal total = calculerTotal();
        BigDecimal remise = total.multiply(pourcentageRemise).divide(BigDecimal.valueOf(100));
        return total.subtract(remise);
    }
}
