package com.gescom.ecole.entity.scolaire;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypePeriode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "periodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Periode extends BaseEntity {

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "designation", nullable = false, length = 100)
    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_periode", nullable = false, length = 20)
    private TypePeriode typePeriode;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "active", nullable = false)
    private Boolean active = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_scolaire_id", nullable = false)
    private AnneeScolaire anneeScolaire;

    public boolean estEnCours() {
        LocalDate aujourd = LocalDate.now();
        return active && aujourd.isAfter(dateDebut.minusDays(1)) && aujourd.isBefore(dateFin.plusDays(1));
    }

    public long calculerDuree() {
        return java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin);
    }
    
    // Getters et Setters manuels
    public void setTypePeriode(TypePeriode typePeriode) {
        this.typePeriode = typePeriode;
    }
    
    // Builder manuel
    public static PeriodeBuilder builder() {
        return new PeriodeBuilder();
    }
    
    public static class PeriodeBuilder {
        private Periode periode = new Periode();
        
        public PeriodeBuilder code(String code) {
            periode.code = code;
            return this;
        }
        
        public PeriodeBuilder designation(String designation) {
            periode.designation = designation;
            return this;
        }
        
        public PeriodeBuilder numero(Integer numero) {
            periode.numero = numero;
            return this;
        }
        
        public PeriodeBuilder active(Boolean active) {
            periode.active = active;
            return this;
        }
        
        public PeriodeBuilder dateDebut(LocalDate dateDebut) {
            periode.dateDebut = dateDebut;
            return this;
        }
        
        public PeriodeBuilder dateFin(LocalDate dateFin) {
            periode.dateFin = dateFin;
            return this;
        }
        
        public PeriodeBuilder typePeriode(TypePeriode typePeriode) {
            periode.typePeriode = typePeriode;
            return this;
        }
        
        public PeriodeBuilder anneeScolaire(AnneeScolaire anneeScolaire) {
            periode.anneeScolaire = anneeScolaire;
            return this;
        }
        
        public Periode build() {
            return periode;
        }
    }
}
