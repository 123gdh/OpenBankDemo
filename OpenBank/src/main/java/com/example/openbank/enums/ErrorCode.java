package com.example.openbank.enums;

public enum  ErrorCode {
    SERVICE_UNAVAILABLE_502("SERVICE_UNAVAILABLE"),
    SERVICE_UNAVAILABLE_503("SERVICE_UNAVAILABLE"),
    SYSTEM_500("SYSTEM"),
    SYSTEM_RETRY_500("SYSTEM_RETRY"),
    PARAM_400("PARAM"),
    SIGN_401("SIGN"),
    FREQUENCY_LIMITED_429("FREQUENCY_LIMITED"),
    RESOURCE_NOT_EXIST_404("RESOURCE_NOT_EXIST");

    public String code;

    public String getcode(){
        return code;
    }

    ErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code='" + code + '\'' +
                '}';
    }
}
