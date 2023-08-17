package com.maxpri.skyengtest.model.entity;

/**
 * @author max_pri
 */
public enum EStatus {
    IN_TRANSIT("In Transit"),
    AT_POST_OFFICE("At Post Office"),
    DELIVERED("Delivered");

    private final String status;

    EStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
