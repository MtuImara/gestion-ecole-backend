package com.gescom.ecole.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TypeBourse {
    MERITE("MERITE", "Mérite"),
    SOCIALE("SOCIALE", "Sociale"),
    SPORTIVE("SPORTIVE", "Sportive"),
    GOUVERNEMENTALE("GOUVERNEMENTALE", "Gouvernementale"),
    PRIVEE("PRIVEE", "Privée");

    private final String key;
    private final String value;

    TypeBourse(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
