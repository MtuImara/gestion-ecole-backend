package com.gescom.ecole.entity.scolaire;

import com.gescom.ecole.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "annees_scolaires",
    uniqueConstraints = @UniqueConstraint(columnNames = "code")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnneeScolaire extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "designation", nullable = false, length = 100)
    private String designation;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "active", nullable = false)
    private Boolean active = false;

    @Column(name = "cloturee", nullable = false)
    private Boolean cloturee = false;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "anneeScolaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Periode> periodes = new ArrayList<>();

    @OneToMany(mappedBy = "anneeScolaire")
    private List<Classe> classes = new ArrayList<>();

    @OneToMany(mappedBy = "anneeScolaire")
    private List<Inscription> inscriptions = new ArrayList<>();

    public void activer() {
        this.active = true;
    }

    public void cloturer() {
        this.cloturee = true;
        this.active = false;
    }

    public boolean estEnCours() {
        LocalDate aujourd = LocalDate.now();
        return !cloturee && aujourd.isAfter(dateDebut.minusDays(1)) && aujourd.isBefore(dateFin.plusDays(1));
    }
    
    // Getters et Setters manuels
    public List<Periode> getPeriodes() {
        return periodes;
    }
    
    public void setPeriodes(List<Periode> periodes) {
        this.periodes = periodes;
    }
}
