package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeContrat {
    CDI("CDI", "Contrat à durée indéterminée"),
    CDD("CDD", "Contrat à durée déterminée"),
    VACATAIRE("VACATAIRE", "Vacataire"),
    STAGIAIRE("STAGIAIRE", "Stagiaire");

    private final String key;
    private final String value;

    TypeContrat(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
