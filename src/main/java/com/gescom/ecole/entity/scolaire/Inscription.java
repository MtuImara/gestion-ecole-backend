package com.gescom.ecole.entity.scolaire;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.StatutInscription;
import com.gescom.ecole.common.enums.TypeInscription;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscriptions",
    uniqueConstraints = @UniqueConstraint(columnNames = "numero_inscription")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscription extends BaseEntity {

    @Column(name = "numero_inscription", nullable = false, unique = true, length = 50)
    private String numeroInscription;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_inscription", nullable = false, length = 20)
    private TypeInscription typeInscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutInscription statut = StatutInscription.EN_ATTENTE;

    @Column(name = "frais_inscription", precision = 10, scale = 2)
    private BigDecimal fraisInscription;

    @Column(name = "frais_payes", nullable = false)
    private Boolean fraisPayes = false;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "valide_par", length = 100)
    private String validePar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_scolaire_id", nullable = false)
    private AnneeScolaire anneeScolaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    public void valider(String validateur) {
        this.statut = StatutInscription.VALIDEE;
        this.dateValidation = LocalDateTime.now();
        this.validePar = validateur;
    }

    public void annuler() {
        this.statut = StatutInscription.ANNULEE;
    }

    public String genererNumero() {
        String prefix = "INS";
        String annee = String.valueOf(LocalDate.now().getYear());
        String sequence = String.format("%06d", getId() != null ? getId() : 1);
        this.numeroInscription = prefix + "-" + annee + "-" + sequence;
        return this.numeroInscription;
    }
}
