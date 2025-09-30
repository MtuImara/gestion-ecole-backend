package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatutInscription {
    EN_ATTENTE("EN_ATTENTE", "En attente"),
    VALIDEE("VALIDEE", "Validée"),
    REJETEE("REJETEE", "Rejetée"),
    ANNULEE("ANNULEE", "Annulée");

    private final String key;
    private final String value;

    StatutInscription(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
