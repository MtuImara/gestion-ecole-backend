package com.gescom.ecole.entity.finance;

import com.gescom.ecole.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "lignes_facture")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneFacture extends BaseEntity {

    @Column(name = "designation", nullable = false, length = 200)
    private String designation;

    @Column(name = "montant_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantUnitaire;

    @Column(name = "quantite", nullable = false)
    private Integer quantite = 1;

    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "tva", precision = 5, scale = 2)
    private BigDecimal tva = BigDecimal.ZERO;

    @Column(name = "remise", precision = 10, scale = 2)
    private BigDecimal remise = BigDecimal.ZERO;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuration_frais_id")
    private ConfigurationFrais configurationFrais;

    public void calculerTotal() {
        BigDecimal montantBrut = montantUnitaire.multiply(BigDecimal.valueOf(quantite));
        BigDecimal montantApresRemise = montantBrut.subtract(remise);
        BigDecimal montantTva = montantApresRemise.multiply(tva).divide(BigDecimal.valueOf(100));
        this.montantTotal = montantApresRemise.add(montantTva);
    }
}
