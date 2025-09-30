package com.gescom.ecole.dto.scolaire;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.gescom.ecole.common.enums.Genre;
import com.gescom.ecole.common.enums.StatutEleve;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EleveDTO {
    
    private Long id;
    
    private String matricule;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;
    
    private String deuxiemePrenom;
    
    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;
    
    private String lieuNaissance;
    
    @NotNull(message = "Le genre est obligatoire")
    private Genre genre;
    
    private String nationalite;
    private String numeroUrgence;
    private String photoUrl;
    private String groupeSanguin;
    private String allergies;
    private String maladiesChroniques;
    private StatutEleve statut;
    private LocalDate dateInscription;
    private String ecoleProvenance;
    private String quartier;
    private Boolean boursier;
    private BigDecimal pourcentageBourse;
    
    // Relations
    @NotNull(message = "La classe est obligatoire")
    private Long classe;
    private ClasseSimpleDTO classeInfo;
    
    private List<Long> parents;
    private List<ParentSimpleDTO> parentsInfo;
    
    // Situation financière
    private BigDecimal soldeTotal;
    private BigDecimal montantPaye;
    private BigDecimal montantDu;
    
    // Calculé
    private Integer age;
    
    @JsonSetter("genre")
    public void setGenreFromString(String genre) {
        if (genre != null) {
            try {
                this.genre = Genre.valueOf(genre.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.genre = null;
            }
        }
    }
    
    @JsonSetter("statut")
    public void setStatutFromString(String statut) {
        if (statut != null) {
            try {
                this.statut = StatutEleve.valueOf(statut.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.statut = StatutEleve.ACTIF;
            }
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClasseSimpleDTO {
        private Long id;
        private String code;
        private String designation;
        private String niveau;
        private Integer effectifActuel;
        private Integer capaciteMax;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParentSimpleDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String typeParent;
        private String telephone;
        private String email;
    }
}
