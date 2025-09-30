package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatutBourse {
    EN_ATTENTE("EN_ATTENTE", "En attente"),
    ACTIVE("ACTIVE", "Active"),
    SUSPENDUE("SUSPENDUE", "Suspendue"),
    TERMINEE("TERMINEE", "Terminée"),
    ANNULEE("ANNULEE", "Annulée");

    private final String key;
    private final String value;

    StatutBourse(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
