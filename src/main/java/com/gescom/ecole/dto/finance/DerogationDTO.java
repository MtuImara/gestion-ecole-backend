package com.gescom.ecole.dto.finance;

import com.gescom.ecole.common.enums.TypeDerogation;
import com.gescom.ecole.common.enums.StatutDerogation;
import com.gescom.ecole.common.enums.NiveauValidation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DerogationDTO {
    
    private Long id;
    
    private String numeroDerogation;
    
    @NotNull(message = "Le type de dérogation est obligatoire")
    private TypeDerogation typeDerogation;
    
    @NotNull(message = "La date de demande est obligatoire")
    private LocalDate dateDemande;
    
    private LocalDate dateDecision;
    
    private StatutDerogation statut;
    
    @NotNull(message = "Le motif est obligatoire")
    @Size(min = 10, max = 1000, message = "Le motif doit contenir entre 10 et 1000 caractères")
    private String motif;
    
    private String justificatifs;
    
    private BigDecimal montantConcerne;
    
    private BigDecimal montantAccorde;
    
    private LocalDate nouvelleEcheance;
    
    private String observations;
    
    private String decidePar;
    
    private NiveauValidation niveauValidation;
    
    @NotNull(message = "L'élève est obligatoire")
    private Long eleveId;
    
    private String eleveNom;
    private String elevePrenom;
    
    @NotNull(message = "Le parent est obligatoire")
    private Long parentId;
    
    private String parentNom;
    
    private Long traiteParId;
    private String traiteParNom;
}
