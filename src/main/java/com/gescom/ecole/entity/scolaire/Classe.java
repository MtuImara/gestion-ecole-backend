package com.gescom.ecole.entity.scolaire;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypeClasse;
import com.gescom.ecole.entity.utilisateur.Enseignant;
import com.gescom.ecole.entity.finance.TarificationClasse;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"code", "annee_scolaire_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classe extends BaseEntity {

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "designation", nullable = false, length = 100)
    private String designation;

    @Column(name = "capacite_max", nullable = false)
    private Integer capaciteMax;

    @Column(name = "effectif_actuel", nullable = false)
    private Integer effectifActuel = 0;

    @Column(name = "salle", length = 50)
    private String salle;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_classe", nullable = false, length = 20)
    private TypeClasse typeClasse;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_scolaire_id", nullable = false)
    private AnneeScolaire anneeScolaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enseignant_principal_id")
    private Enseignant enseignantPrincipal;

    @OneToMany(mappedBy = "classe")
    private List<Eleve> eleves = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "classe_enseignants",
        joinColumns = @JoinColumn(name = "classe_id"),
        inverseJoinColumns = @JoinColumn(name = "enseignant_id")
    )
    private List<Enseignant> enseignants = new ArrayList<>();

    @OneToOne(mappedBy = "classe", cascade = CascadeType.ALL, orphanRemoval = true)
    private TarificationClasse tarification;

    public void ajouterEleve(Eleve eleve) {
        if (effectifActuel < capaciteMax) {
            eleves.add(eleve);
            eleve.setClasse(this);
            effectifActuel++;
        }
    }

    public boolean estPleine() {
        return effectifActuel >= capaciteMax;
    }

    public double calculerTauxRemplissage() {
        return (effectifActuel * 100.0) / capaciteMax;
    }
    
    // Getters et Setters manuels pour contourner le problème Lombok
    public String getDesignation() {
        return designation;
    }
    
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    
    public Integer getEffectifActuel() {
        return effectifActuel;
    }
    
    public void setEffectifActuel(Integer effectifActuel) {
        this.effectifActuel = effectifActuel;
    }
    
    public TypeClasse getTypeClasse() {
        return typeClasse;
    }
    
    public void setTypeClasse(TypeClasse typeClasse) {
        this.typeClasse = typeClasse;
    }
    
    public Niveau getNiveau() {
        return niveau;
    }
    
    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }
    
    public AnneeScolaire getAnneeScolaire() {
        return anneeScolaire;
    }
    
    public void setAnneeScolaire(AnneeScolaire anneeScolaire) {
        this.anneeScolaire = anneeScolaire;
    }
    
    // Builder manuel
    public static ClasseBuilder builder() {
        return new ClasseBuilder();
    }
    
    public static class ClasseBuilder {
        private Classe classe = new Classe();
        
        public ClasseBuilder code(String code) {
            classe.code = code;
            return this;
        }
        
        public ClasseBuilder designation(String designation) {
            classe.designation = designation;
            return this;
        }
        
        public ClasseBuilder capaciteMax(Integer capaciteMax) {
            classe.capaciteMax = capaciteMax;
            return this;
        }
        
        public ClasseBuilder effectifActuel(Integer effectifActuel) {
            classe.effectifActuel = effectifActuel;
            return this;
        }
        
        public ClasseBuilder typeClasse(TypeClasse typeClasse) {
            classe.typeClasse = typeClasse;
            return this;
        }
        
        public ClasseBuilder niveau(Niveau niveau) {
            classe.niveau = niveau;
            return this;
        }
        
        public ClasseBuilder anneeScolaire(AnneeScolaire anneeScolaire) {
            classe.anneeScolaire = anneeScolaire;
            return this;
        }
        
        public ClasseBuilder enseignantPrincipal(Enseignant enseignant) {
            classe.enseignantPrincipal = enseignant;
            return this;
        }
        
        public Classe build() {
            return classe;
        }
    }
}
