package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FrequencePaiement {
    UNIQUE("UNIQUE", "Unique"),
    MENSUEL("MENSUEL", "Mensuel"),
    TRIMESTRIEL("TRIMESTRIEL", "Trimestriel"),
    SEMESTRIEL("SEMESTRIEL", "Semestriel"),
    ANNUEL("ANNUEL", "Annuel");

    private final String key;
    private final String value;

    FrequencePaiement(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
