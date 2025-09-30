package com.gescom.ecole.entity.reporting;

import com.gescom.ecole.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistiques", indexes = {
    @Index(name = "idx_stat_type_periode", columnList = "type_statistique, periode"),
    @Index(name = "idx_stat_dates", columnList = "date_debut, date_fin"),
    @Index(name = "idx_stat_entite", columnList = "entite_liee_type, entite_liee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Statistique extends BaseEntity {

    @Column(name = "type_statistique", nullable = false, length = 100)
    private String typeStatistique;

    @Column(name = "categorie", nullable = false, length = 100)
    private String categorie;

    @Column(name = "periode", nullable = false, length = 50)
    private String periode; // JOUR, SEMAINE, MOIS, TRIMESTRE, ANNEE

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "valeur_numerique", precision = 19, scale = 2)
    private BigDecimal valeurNumerique;

    @Column(name = "valeur_entiere")
    private Long valeurEntiere;

    @Column(name = "valeur_pourcentage", precision = 5, scale = 2)
    private BigDecimal valeurPourcentage;

    @Column(name = "valeur_texte", length = 500)
    private String valeurTexte;

    @Column(name = "donnees_json", columnDefinition = "TEXT")
    private String donneesJson; // Pour stocker des données complexes en JSON

    @Column(name = "date_calcul", nullable = false)
    private LocalDateTime dateCalcul;

    @Column(name = "entite_liee_type", length = 100)
    private String entiteLieeType; // CLASSE, NIVEAU, ELEVE, etc.

    @Column(name = "entite_liee_id")
    private Long entiteLieeId;

    @Column(name = "annee_scolaire_id")
    private Long anneeScolaireId;

    @Column(name = "periode_id")
    private Long periodeId;

    // Getters et Setters manuels
    public Long getId() {
        // Hérité de BaseEntity, mais ajouté ici pour compatibilité
        return null; // Sera implémenté par BaseEntity
    }
    
    public String getTypeStatistique() {
        return typeStatistique;
    }
    
    public void setTypeStatistique(String typeStatistique) {
        this.typeStatistique = typeStatistique;
    }
    
    public String getCategorie() {
        return categorie;
    }
    
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    
    // Méthodes utilitaires
    public boolean estValide() {
        return dateCalcul != null && 
               LocalDateTime.now().minusDays(1).isBefore(dateCalcul);
    }
    
    // Builder manuel
    public static StatistiqueBuilder builder() {
        return new StatistiqueBuilder();
    }
    
    public static class StatistiqueBuilder {
        private Statistique statistique = new Statistique();
        
        public StatistiqueBuilder typeStatistique(String typeStatistique) {
            statistique.typeStatistique = typeStatistique;
            return this;
        }
        
        public StatistiqueBuilder categorie(String categorie) {
            statistique.categorie = categorie;
            return this;
        }
        
        public StatistiqueBuilder periode(String periode) {
            statistique.periode = periode;
            return this;
        }
        
        public StatistiqueBuilder dateDebut(LocalDate dateDebut) {
            statistique.dateDebut = dateDebut;
            return this;
        }
        
        public StatistiqueBuilder dateFin(LocalDate dateFin) {
            statistique.dateFin = dateFin;
            return this;
        }
        
        public StatistiqueBuilder valeurNumerique(BigDecimal valeurNumerique) {
            statistique.valeurNumerique = valeurNumerique;
            return this;
        }
        
        public StatistiqueBuilder valeurEntiere(Long valeurEntiere) {
            statistique.valeurEntiere = valeurEntiere;
            return this;
        }
        
        public StatistiqueBuilder valeurPourcentage(BigDecimal valeurPourcentage) {
            statistique.valeurPourcentage = valeurPourcentage;
            return this;
        }
        
        public StatistiqueBuilder valeurTexte(String valeurTexte) {
            statistique.valeurTexte = valeurTexte;
            return this;
        }
        
        public StatistiqueBuilder donneesJson(String donneesJson) {
            statistique.donneesJson = donneesJson;
            return this;
        }
        
        public StatistiqueBuilder dateCalcul(LocalDateTime dateCalcul) {
            statistique.dateCalcul = dateCalcul;
            return this;
        }
        
        public StatistiqueBuilder anneeScolaireId(Long anneeScolaireId) {
            statistique.anneeScolaireId = anneeScolaireId;
            return this;
        }
        
        public Statistique build() {
            return statistique;
        }
    }
}
