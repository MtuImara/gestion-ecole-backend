package com.gescom.ecole.entity.scolaire;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypeNiveau;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "niveaux",
    uniqueConstraints = @UniqueConstraint(columnNames = "code")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Niveau extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "designation", nullable = false, length = 100)
    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_niveau", nullable = false, length = 20)
    private TypeNiveau typeNiveau;

    @Column(name = "ordre", nullable = false)
    private Integer ordre;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "niveau", cascade = CascadeType.ALL)
    private List<Classe> classes = new ArrayList<>();

    public void ajouterClasse(Classe classe) {
        classes.add(classe);
        classe.setNiveau(this);
    }

    public int calculerEffectif() {
        return classes.stream()
            .mapToInt(Classe::getEffectifActuel)
            .sum();
    }
    
    // Getters et Setters manuels
    public String getDesignation() {
        return designation;
    }
    
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    
    public TypeNiveau getTypeNiveau() {
        return typeNiveau;
    }
    
    public void setTypeNiveau(TypeNiveau typeNiveau) {
        this.typeNiveau = typeNiveau;
    }
    
    // Builder manuel
    public static NiveauBuilder builder() {
        return new NiveauBuilder();
    }
    
    public static class NiveauBuilder {
        private Niveau niveau = new Niveau();
        
        public NiveauBuilder code(String code) {
            niveau.code = code;
            return this;
        }
        
        public NiveauBuilder designation(String designation) {
            niveau.designation = designation;
            return this;
        }
        
        public NiveauBuilder typeNiveau(TypeNiveau typeNiveau) {
            niveau.typeNiveau = typeNiveau;
            return this;
        }
        
        public NiveauBuilder ordre(Integer ordre) {
            niveau.ordre = ordre;
            return this;
        }
        
        public Niveau build() {
            return niveau;
        }
    }
}
