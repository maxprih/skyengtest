package com.maxpri.skyengtest.model.entity;

/**
 * @author max_pri
 */
public enum EType {
    LETTER("Letter"),
    PACKAGE("Package"),
    PARCEL("Parcel"),
    POSTCARD("Postcard");

    private final String type;

    EType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
