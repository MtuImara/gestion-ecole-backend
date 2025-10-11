package com.gescom.ecole.dto.scolaire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EleveSimpleDTO {
    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String classe;
    private String statut;
}
