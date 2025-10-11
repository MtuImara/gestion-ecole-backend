package com.gescom.ecole.entity.scolaire;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.Genre;
import com.gescom.ecole.common.enums.StatutEleve;
import com.gescom.ecole.entity.utilisateur.Parent;
import com.gescom.ecole.entity.finance.Facture;
import com.gescom.ecole.entity.finance.Bourse;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "eleves",
    uniqueConstraints = @UniqueConstraint(columnNames = "matricule"),
    indexes = {
        @Index(name = "idx_eleve_matricule", columnList = "matricule"),
        @Index(name = "idx_eleve_nom_prenom", columnList = "nom, prenom")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Eleve extends BaseEntity {

    @Column(name = "matricule", nullable = false, unique = true, length = 50)
    private String matricule;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "deuxieme_prenom", length = 100)
    private String deuxiemePrenom;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance", length = 100)
    private String lieuNaissance;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false, length = 20)
    private Genre genre;

    @Column(name = "nationalite", length = 50)
    private String nationalite;

    @Column(name = "numero_urgence", length = 20)
    private String numeroUrgence;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "groupe_sanguin", length = 10)
    private String groupeSanguin;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "maladies_chroniques", columnDefinition = "TEXT")
    private String maladiesChroniques;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutEleve statut = StatutEleve.ACTIF;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @Column(name = "ecole_provenance", length = 200)
    private String ecoleProvenance;

    @Column(name = "adresse_domicile", length = 255)
    private String adresseDomicile;

    @Column(name = "quartier", length = 100)
    private String quartier;

    @Column(name = "boursier", nullable = false)
    private Boolean boursier = false;

    @Column(name = "pourcentage_bourse", precision = 5, scale = 2)
    private BigDecimal pourcentageBourse = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id")
    private Classe classe;

    @ManyToMany
    @JoinTable(
        name = "eleve_parents",
        joinColumns = @JoinColumn(name = "eleve_id"),
        inverseJoinColumns = @JoinColumn(name = "parent_id")
    )
    private Set<Parent> parents = new HashSet<>();

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL)
    private List<Inscription> inscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "eleve")
    private List<Facture> factures = new ArrayList<>();

    @OneToMany(mappedBy = "eleve")
    private List<Bourse> bourses = new ArrayList<>();

    public void inscrire(Classe classe) {
        this.classe = classe;
        this.statut = StatutEleve.ACTIF;
        classe.ajouterEleve(this);
    }

    public void reinscrire(Classe nouvelleClasse) {
        if (this.classe != null) {
            this.classe.getEleves().remove(this);
            this.classe.setEffectifActuel(this.classe.getEffectifActuel() - 1);
        }
        inscrire(nouvelleClasse);
    }

    public void transferer(Classe nouvelleClasse) {
        reinscrire(nouvelleClasse);
        this.statut = StatutEleve.TRANSFERE;
    }

    public void suspendre() {
        this.statut = StatutEleve.SUSPENDU;
    }

    public int calculerAge() {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    public void addParent(Parent parent) {
        parents.add(parent);
        parent.getEnfants().add(this);
    }

    public void removeParent(Parent parent) {
        parents.remove(parent);
        parent.getEnfants().remove(this);
    }
    
    // Getters et Setters manuels pour contourner le problème Lombok
    public String getMatricule() {
        return matricule;
    }
    
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
    
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }
    
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    public Classe getClasse() {
        return classe;
    }
    
    public void setClasse(Classe classe) {
        this.classe = classe;
    }
    
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
    
    public Genre getGenre() {
        return genre;
    }
    
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    
    public StatutEleve getStatut() {
        return statut;
    }
    
    public void setStatut(StatutEleve statut) {
        this.statut = statut;
    }
    
    public List<Facture> getFactures() {
        return factures;
    }
    
    public void setFactures(List<Facture> factures) {
        this.factures = factures;
    }
    
    public Set<Parent> getParents() {
        return parents;
    }
    
    public void setParents(Set<Parent> parents) {
        this.parents = parents;
    }
    
    // Builder manuel
    public static EleveBuilder builder() {
        return new EleveBuilder();
    }
    
    public static class EleveBuilder {
        private Eleve eleve = new Eleve();
        
        public EleveBuilder matricule(String matricule) {
            eleve.matricule = matricule;
            return this;
        }
        
        public EleveBuilder nom(String nom) {
            eleve.nom = nom;
            return this;
        }
        
        public EleveBuilder prenom(String prenom) {
            eleve.prenom = prenom;
            return this;
        }
        
        public EleveBuilder dateNaissance(java.time.LocalDate dateNaissance) {
            eleve.dateNaissance = dateNaissance;
            return this;
        }
        
        public EleveBuilder lieuNaissance(String lieuNaissance) {
            eleve.lieuNaissance = lieuNaissance;
            return this;
        }
        
        public EleveBuilder genre(Genre genre) {
            eleve.genre = genre;
            return this;
        }
        
        public EleveBuilder nationalite(String nationalite) {
            eleve.nationalite = nationalite;
            return this;
        }
        
        public EleveBuilder numeroUrgence(String numeroUrgence) {
            eleve.numeroUrgence = numeroUrgence;
            return this;
        }
        
        public EleveBuilder dateInscription(LocalDate dateInscription) {
            eleve.dateInscription = dateInscription;
            return this;
        }
        
        public EleveBuilder adresseDomicile(String adresseDomicile) {
            eleve.adresseDomicile = adresseDomicile;
            return this;
        }
        
        public EleveBuilder quartier(String quartier) {
            eleve.quartier = quartier;
            return this;
        }
        
        public EleveBuilder boursier(Boolean boursier) {
            eleve.boursier = boursier;
            return this;
        }
        
        public EleveBuilder pourcentageBourse(BigDecimal pourcentageBourse) {
            eleve.pourcentageBourse = pourcentageBourse;
            return this;
        }
        
        public EleveBuilder parents(Set<Parent> parents) {
            eleve.parents = parents;
            return this;
        }
        
        public EleveBuilder statut(StatutEleve statut) {
            eleve.statut = statut;
            return this;
        }
        
        public EleveBuilder classe(Classe classe) {
            eleve.classe = classe;
            return this;
        }
        
        public Eleve build() {
            return eleve;
        }
    }
}
