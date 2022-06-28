package com.example.openbank.enums;

public enum ResponseStatus {
    SUCCESS(2000),
    SUCC(0),
    ERROR(5000);

    Integer status;

    @Override
    public String toString() {
        return "ResponseStatus{" +
                "status=" + status +
                '}';
    }

    public Integer getdesc(){
        return status;
    }
    ResponseStatus(Integer status) {
        this.status = status;
    }
}
