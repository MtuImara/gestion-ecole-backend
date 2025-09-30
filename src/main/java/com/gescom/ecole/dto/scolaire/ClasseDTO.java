package com.gescom.ecole.dto.scolaire;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.gescom.ecole.common.enums.TypeClasse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClasseDTO {
    
    private Long id;
    
    @NotBlank(message = "Le code est obligatoire")
    @Size(max = 20, message = "Le code ne doit pas dépasser 20 caractères")
    private String code;
    
    @NotBlank(message = "La désignation est obligatoire")
    @Size(max = 100, message = "La désignation ne doit pas dépasser 100 caractères")
    private String designation;
    
    @NotNull(message = "La capacité maximale est obligatoire")
    @Min(value = 1, message = "La capacité maximale doit être au moins 1")
    private Integer capaciteMax;
    
    private Integer effectifActuel;
    
    private String salle;
    
    @NotNull(message = "Le type de classe est obligatoire")
    private TypeClasse typeClasse;
    
    private Boolean active;
    
    // Relations
    @NotNull(message = "Le niveau est obligatoire")
    private Long niveau;
    private String niveauDesignation;
    
    @NotNull(message = "L'année scolaire est obligatoire")
    private Long anneeScolaire;
    private String anneeScolaireDesignation;
    
    private Long enseignantPrincipal;
    private String enseignantPrincipalNom;
    
    // Calculé
    private Integer placesDisponibles;
    private Double tauxRemplissage;
    
    @JsonSetter("typeClasse")
    public void setTypeClasseFromString(String typeClasse) {
        if (typeClasse != null) {
            try {
                this.typeClasse = TypeClasse.valueOf(typeClasse.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.typeClasse = TypeClasse.NORMALE;
            }
        }
    }
}
