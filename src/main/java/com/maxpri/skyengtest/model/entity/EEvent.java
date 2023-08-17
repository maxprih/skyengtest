package com.maxpri.skyengtest.model.entity;

/**
 * @author max_pri
 */
public enum EEvent {
    SENT("Sent"),
    RECEIVED("Received"),
    DELIVERED("Delivered"),
    REGISTERED("Registered");

    private final String type;

    EEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
