package com.gescom.ecole.dto.communication;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.gescom.ecole.common.enums.TypeNotification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    
    private Long id;
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne doit pas dépasser 255 caractères")
    private String titre;
    
    @NotBlank(message = "Le message est obligatoire")
    private String message;
    
    @NotNull(message = "Le type de notification est obligatoire")
    private TypeNotification type;
    
    private LocalDateTime dateEnvoi;
    private LocalDateTime dateLecture;
    
    @Builder.Default
    private Boolean lu = false;
    
    @Builder.Default
    private Integer priorite = 0; // 0: normale, 1: importante, 2: urgente
    
    private String lienAction;
    private Map<String, Object> donneesSupplementaires;
    
    @NotNull(message = "L'utilisateur est obligatoire")
    private Long utilisateur;
    private UtilisateurSimpleDTO utilisateurInfo;
    
    private LocalDateTime expireLe;
    private Boolean expiree;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @JsonSetter("type")
    public void setTypeFromString(String type) {
        if (type != null) {
            try {
                this.type = TypeNotification.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.type = null;
            }
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UtilisateurSimpleDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private String role;
    }
}
