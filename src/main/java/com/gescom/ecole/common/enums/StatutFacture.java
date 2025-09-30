package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatutFacture {
    BROUILLON("BROUILLON", "Brouillon"),
    EMISE("EMISE", "Émise"),
    PARTIELLEMENT_PAYEE("PARTIELLEMENT_PAYEE", "Partiellement payée"),
    PAYEE("PAYEE", "Payée"),
    ANNULEE("ANNULEE", "Annulée"),
    EN_RETARD("EN_RETARD", "En retard");

    private final String key;
    private final String value;

    StatutFacture(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
