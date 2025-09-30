package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeRecu {
    PAIEMENT("PAIEMENT", "Paiement"),
    ACOMPTE("ACOMPTE", "Acompte"),
    REMBOURSEMENT("REMBOURSEMENT", "Remboursement"),
    AVOIR("AVOIR", "Avoir");

    private final String key;
    private final String value;

    TypeRecu(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
