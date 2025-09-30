package com.gescom.ecole.entity.reporting;

import com.gescom.ecole.common.entity.BaseEntity;
import com.gescom.ecole.common.enums.TypeRapport;
import com.gescom.ecole.entity.utilisateur.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "rapports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"generePar"})
public class Rapport extends BaseEntity {

    @Column(name = "nom", nullable = false, length = 255)
    private String nom;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private TypeRapport type;

    @Column(name = "date_generation", nullable = false)
    private LocalDateTime dateGeneration;

    @Column(name = "periode_debut")
    private LocalDateTime periodeDebut;

    @Column(name = "periode_fin")
    private LocalDateTime periodeFin;

    @Column(name = "format_export", length = 20)
    private String formatExport; // PDF, EXCEL, CSV

    @Column(name = "chemin_fichier", length = 500)
    private String cheminFichier;

    @Column(name = "taille_fichier")
    private Long tailleFichier;

    @ElementCollection
    @CollectionTable(name = "rapport_parametres", joinColumns = @JoinColumn(name = "rapport_id"))
    @MapKeyColumn(name = "cle")
    @Column(name = "valeur")
    private Map<String, String> parametres = new HashMap<>();

    @Column(name = "statut", length = 50)
    private String statut; // EN_COURS, TERMINE, ERREUR

    @Column(name = "message_erreur", columnDefinition = "TEXT")
    private String messageErreur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genere_par_id", nullable = false)
    private Utilisateur generePar;

    @Column(name = "temps_execution")
    private Long tempsExecution; // en millisecondes

    @Column(name = "nombre_lignes")
    private Integer nombreLignes;

    // Méthodes utilitaires
    public void ajouterParametre(String cle, String valeur) {
        parametres.put(cle, valeur);
    }

    public String getParametre(String cle) {
        return parametres.get(cle);
    }
    
    // Getters et Setters manuels
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public TypeRapport getType() {
        return type;
    }
    
    public void setType(TypeRapport type) {
        this.type = type;
    }
    
    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }
    
    public void setDateGeneration(LocalDateTime dateGeneration) {
        this.dateGeneration = dateGeneration;
    }
}
