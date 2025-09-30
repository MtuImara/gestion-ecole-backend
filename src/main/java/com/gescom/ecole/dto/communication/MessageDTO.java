package com.gescom.ecole.dto.communication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    
    private Long id;
    
    @NotBlank(message = "L'objet du message est obligatoire")
    @Size(max = 255, message = "L'objet ne doit pas dépasser 255 caractères")
    private String objet;
    
    @NotBlank(message = "Le contenu du message est obligatoire")
    private String contenu;
    
    private LocalDateTime dateEnvoi;
    
    @Builder.Default
    private Boolean important = false;
    
    @Builder.Default
    private Boolean brouillon = false;
    
    @NotNull(message = "L'expéditeur est obligatoire")
    private Long expediteur;
    private ExpediteurDTO expediteurInfo;
    
    @NotEmpty(message = "Au moins un destinataire est requis")
    private List<Long> destinataires;
    private List<DestinataireDTO> destinatairesInfo;
    
    private List<PieceJointeDTO> piecesJointes;
    
    private MessageLectureDTO statutLecture; // Pour l'utilisateur courant
    
    private Integer nombreDestinataires;
    private Integer nombrePiecesJointes;
    private Boolean lu; // Pour l'utilisateur courant
    private Boolean archive; // Pour l'utilisateur courant
    private Boolean favori; // Pour l'utilisateur courant
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpediteurDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private String role;
        private String photoUrl;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DestinataireDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private String role;
        private Boolean lu;
        private LocalDateTime dateLecture;
    }
}
