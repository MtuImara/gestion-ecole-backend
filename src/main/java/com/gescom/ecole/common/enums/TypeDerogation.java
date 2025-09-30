package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeDerogation {
    DELAI_PAIEMENT("DELAI_PAIEMENT", "Délai de paiement"),
    REDUCTION_FRAIS("REDUCTION_FRAIS", "Réduction de frais"),
    EXONERATION("EXONERATION", "Exonération"),
    ECHELONNEMENT("ECHELONNEMENT", "Échelonnement"),
    AUTRE("AUTRE", "Autre");

    private final String key;
    private final String value;

    TypeDerogation(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
