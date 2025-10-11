package com.gescom.ecole.dto.utilisateur;

import com.gescom.ecole.common.enums.TypeParent;
import com.gescom.ecole.dto.scolaire.EleveSimpleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentDTO {
    
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;
    
    @Email(message = "L'email doit être valide")
    private String email;
    
    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[+]?[0-9\\s-]+$", message = "Le numéro de téléphone n'est pas valide")
    private String telephone;
    
    private String telephoneSecondaire;
    
    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    private String adresse;
    
    @Size(max = 100, message = "La profession ne doit pas dépasser 100 caractères")
    private String profession;
    
    @Size(max = 150, message = "L'employeur ne doit pas dépasser 150 caractères")
    private String employeur;
    
    @Size(max = 255, message = "L'adresse de travail ne doit pas dépasser 255 caractères")
    private String adresseTravail;
    
    private TypeParent typeParent;
    
    private String lienParente; // Pour compatibilité avec le frontend
    
    @Size(max = 50, message = "Le CIN ne doit pas dépasser 50 caractères")
    private String cin;
    
    @Size(max = 50, message = "Le numéro de passeport ne doit pas dépasser 50 caractères")
    private String numeroPasseport;
    
    private String numeroParent;
    
    private Boolean actif;
    
    private LocalDateTime dateCreation;
    
    private LocalDateTime dateModification;
    
    // Relations
    @Builder.Default
    private List<EleveSimpleDTO> enfants = new ArrayList<>();
    
    @Builder.Default
    private List<Long> enfantIds = new ArrayList<>();
    
    // Statistiques
    private Integer nombreEnfants;
    
    private Double montantTotalDu;
    
    private Double montantTotalPaye;
    
    // Méthode utilitaire pour obtenir le nom complet
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    // Méthode pour convertir lienParente en TypeParent
    public void updateTypeParentFromLienParente() {
        if (lienParente != null) {
            switch (lienParente.toUpperCase()) {
                case "PERE":
                    this.typeParent = TypeParent.PERE;
                    break;
                case "MERE":
                    this.typeParent = TypeParent.MERE;
                    break;
                case "TUTEUR":
                    this.typeParent = TypeParent.TUTEUR;
                    break;
                case "AUTRE":
                    this.typeParent = TypeParent.AUTRE;
                    break;
            }
        }
    }
    
    // Méthode pour convertir TypeParent en lienParente
    public void updateLienParenteFromTypeParent() {
        if (typeParent != null) {
            this.lienParente = typeParent.name();
        }
    }
}
