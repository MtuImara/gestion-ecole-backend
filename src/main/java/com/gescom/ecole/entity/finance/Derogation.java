package com.gescom.ecole.entity.finance;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypeDerogation;
import com.gescom.ecole.common.enums.StatutDerogation;
import com.gescom.ecole.common.enums.NiveauValidation;
import com.gescom.ecole.entity.scolaire.Eleve;
import com.gescom.ecole.entity.utilisateur.Parent;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "derogations",
    uniqueConstraints = @UniqueConstraint(columnNames = "numero_derogation")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Derogation extends BaseEntity {

    @Column(name = "numero_derogation", nullable = false, unique = true, length = 50)
    private String numeroDerogation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_derogation", nullable = false, length = 30)
    private TypeDerogation typeDerogation;

    @Column(name = "date_demande", nullable = false)
    private LocalDate dateDemande;

    @Column(name = "date_decision")
    private LocalDate dateDecision;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutDerogation statut = StatutDerogation.EN_ATTENTE;

    @Column(name = "motif", nullable = false, columnDefinition = "TEXT")
    private String motif;

    @Column(name = "justificatifs", columnDefinition = "TEXT")
    private String justificatifs;

    @Column(name = "montant_concerne", precision = 10, scale = 2)
    private BigDecimal montantConcerne;

    @Column(name = "montant_accorde", precision = 10, scale = 2)
    private BigDecimal montantAccorde;

    @Column(name = "nouvelle_echeance")
    private LocalDate nouvelleEcheance;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "decide_par", length = 100)
    private String decidePar;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_validation", length = 30)
    private NiveauValidation niveauValidation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traite_par_id")
    private Utilisateur traitePar;

    @ManyToMany(mappedBy = "derogations")
    private List<Facture> factures = new ArrayList<>();

    public void soumettre() {
        this.statut = StatutDerogation.EN_ATTENTE;
        this.dateDemande = LocalDate.now();
        genererNumero();
    }

    public void approuver(String decideur, BigDecimal montantAccorde) {
        this.statut = StatutDerogation.APPROUVEE;
        this.dateDecision = LocalDate.now();
        this.decidePar = decideur;
        this.montantAccorde = montantAccorde;
    }

    public void rejeter(String decideur, String motifRejet) {
        this.statut = StatutDerogation.REJETEE;
        this.dateDecision = LocalDate.now();
        this.decidePar = decideur;
        this.observations = motifRejet;
    }

    public void annuler() {
        this.statut = StatutDerogation.ANNULEE;
    }

    private String genererNumero() {
        String prefix = "DER";
        String annee = String.valueOf(LocalDate.now().getYear());
        String sequence = String.format("%06d", getId() != null ? getId() : 1);
        this.numeroDerogation = prefix + "-" + annee + "-" + sequence;
        return this.numeroDerogation;
    }
}
