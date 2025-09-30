package com.gescom.ecole.entity.utilisateur;

import com.gescom.ecole.common.enums.TypeParent;
import com.gescom.ecole.entity.scolaire.Eleve;
import com.gescom.ecole.entity.finance.Paiement;
import com.gescom.ecole.entity.finance.Derogation;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parents")
@DiscriminatorValue("PARENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parent extends Utilisateur {

    @Column(name = "numero_parent", unique = true, length = 50)
    private String numeroParent;

    @Column(name = "profession", length = 100)
    private String profession;

    @Column(name = "employeur", length = 150)
    private String employeur;

    @Column(name = "adresse_travail", length = 255)
    private String adresseTravail;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_parent", length = 20)
    private TypeParent typeParent;

    @Column(name = "cin", length = 50)
    private String cin;

    @Column(name = "numero_passeport", length = 50)
    private String numeroPasseport;

    @ManyToMany(mappedBy = "parents")
    private Set<Eleve> enfants = new HashSet<>();

    @OneToMany(mappedBy = "parent")
    private Set<Paiement> paiements = new HashSet<>();

    @OneToMany(mappedBy = "parent")
    private Set<Derogation> derogations = new HashSet<>();

    public BigDecimal consulterSolde() {
        // Calcul du solde total pour tous les enfants
        return enfants.stream()
            .flatMap(eleve -> eleve.getFactures().stream())
            .map(facture -> facture.getMontantRestant())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters et Setters manuels pour contourner le problème Lombok
    public String getNumeroParent() {
        return numeroParent;
    }
    
    public void setNumeroParent(String numeroParent) {
        this.numeroParent = numeroParent;
    }
    
    public String getProfession() {
        return profession;
    }
    
    public void setProfession(String profession) {
        this.profession = profession;
    }
    
    public TypeParent getTypeParent() {
        return typeParent;
    }
    
    public void setTypeParent(TypeParent typeParent) {
        this.typeParent = typeParent;
    }
    
    public String getCin() {
        return cin;
    }
    
    public void setCin(String cin) {
        this.cin = cin;
    }
}
