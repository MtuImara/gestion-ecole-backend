package com.gescom.ecole.dto.communication;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageLectureDTO {
    
    private Long id;
    private Long message;
    private Long utilisateur;
    private LocalDateTime dateLecture;
    
    @Builder.Default
    private Boolean lu = false;
    
    @Builder.Default
    private Boolean archive = false;
    
    @Builder.Default
    private Boolean corbeille = false;
    
    @Builder.Default
    private Boolean favori = false;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
