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

    @Column(name = "nom", length = 100)
    private String nom;
    
    @Column(name = "prenom", length = 100)
    private String prenom;
    
    @Column(name = "adresse", length = 255)
    private String adresse;

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
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
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
    
    public String getEmployeur() {
        return employeur;
    }
    
    public void setEmployeur(String employeur) {
        this.employeur = employeur;
    }
    
    public String getAdresseTravail() {
        return adresseTravail;
    }
    
    public void setAdresseTravail(String adresseTravail) {
        this.adresseTravail = adresseTravail;
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
    
    public String getNumeroPasseport() {
        return numeroPasseport;
    }
    
    public void setNumeroPasseport(String numeroPasseport) {
        this.numeroPasseport = numeroPasseport;
    }
    
    public Set<Eleve> getEnfants() {
        return enfants;
    }
    
    public void setEnfants(Set<Eleve> enfants) {
        this.enfants = enfants;
    }
}
