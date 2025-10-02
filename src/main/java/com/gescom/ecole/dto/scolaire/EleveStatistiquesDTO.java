package com.gescom.ecole.dto.scolaire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EleveStatistiquesDTO {
    private Long total;
    private Long actifs;
    private Long boursiers;
    private Long inactifs;
}
