package com.gescom.ecole.dto.communication;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.gescom.ecole.common.enums.TypeAnnonce;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceDTO {
    
    private Long id;
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne doit pas dépasser 255 caractères")
    private String titre;
    
    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;
    
    @NotNull(message = "Le type d'annonce est obligatoire")
    private TypeAnnonce type;
    
    private LocalDateTime datePublication;
    
    @NotNull(message = "La date de début d'affichage est obligatoire")
    private LocalDateTime dateDebutAffichage;
    
    private LocalDateTime dateFinAffichage;
    
    @Builder.Default
    private Integer priorite = 0; // 0: normale, 1: importante, 2: urgente
    
    @Builder.Default
    private Boolean active = true;
    
    @Builder.Default
    private Boolean epinglee = false;
    
    @NotNull(message = "L'auteur est obligatoire")
    private Long auteur;
    private AuteurDTO auteurInfo;
    
    @Builder.Default
    private List<String> destinataires = new ArrayList<>();
    
    private List<PieceJointeDTO> piecesJointes;
    
    @Builder.Default
    private Integer nombreVues = 0;
    
    private Boolean estActive;
    private Integer nombrePiecesJointes;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @JsonSetter("type")
    public void setTypeFromString(String type) {
        if (type != null) {
            try {
                this.type = TypeAnnonce.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.type = null;
            }
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuteurDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private String role;
        private String photoUrl;
    }
}
