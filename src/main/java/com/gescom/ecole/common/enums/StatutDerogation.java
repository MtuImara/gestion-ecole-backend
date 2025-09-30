package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatutDerogation {
    EN_ATTENTE("EN_ATTENTE", "En attente"),
    APPROUVEE("APPROUVEE", "Approuvée"),
    REJETEE("REJETEE", "Rejetée"),
    ANNULEE("ANNULEE", "Annulée"),
    EXPIREE("EXPIREE", "Expirée");

    private final String key;
    private final String value;

    StatutDerogation(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
