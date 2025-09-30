package com.gescom.ecole.dto.communication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PieceJointeDTO {
    
    private Long id;
    
    @NotBlank(message = "Le nom du fichier est obligatoire")
    @Size(max = 255, message = "Le nom du fichier ne doit pas dépasser 255 caractères")
    private String nomFichier;
    
    @NotBlank(message = "Le nom original est obligatoire")
    @Size(max = 255, message = "Le nom original ne doit pas dépasser 255 caractères")
    private String nomOriginal;
    
    @NotBlank(message = "Le type MIME est obligatoire")
    @Size(max = 100, message = "Le type MIME ne doit pas dépasser 100 caractères")
    private String typeMime;
    
    @NotNull(message = "La taille du fichier est obligatoire")
    private Long taille;
    
    @NotBlank(message = "Le chemin du fichier est obligatoire")
    @Size(max = 500, message = "Le chemin ne doit pas dépasser 500 caractères")
    private String cheminFichier;
    
    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;
    
    private Long message;
    private Long annonce;
    
    private String tailleFormatee;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
