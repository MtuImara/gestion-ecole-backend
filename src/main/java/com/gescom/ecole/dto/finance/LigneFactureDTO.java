package com.gescom.ecole.dto.finance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneFactureDTO {
    
    private Long id;
    
    @NotBlank(message = "La désignation est obligatoire")
    private String designation;
    
    @NotNull(message = "Le montant unitaire est obligatoire")
    @Positive(message = "Le montant unitaire doit être positif")
    private BigDecimal montantUnitaire;
    
    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Integer quantite;
    
    private BigDecimal montantTotal;
    private BigDecimal tva;
    private BigDecimal remise;
    private String description;
    
    private Long configurationFraisId;
}
