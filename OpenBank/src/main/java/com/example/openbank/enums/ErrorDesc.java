package com.example.openbank.enums;


import com.fasterxml.jackson.annotation.JsonProperty;

public enum ErrorDesc {
    SERVICE_UNAVAILABLE_502("服务下线，暂时不可用"),
    SERVICE_UNAVAILABLE_503("服务不可用，过载保护"),
    SYSTEM_500("系统繁忙"),
    SYSTEM_RETRY_500("系统繁忙，请稍后重试"),
    PARAM_400("参数错误"),
    SIGN_401("签名验证失败"),
    FREQUENCY_LIMITED_429("频率限制"),
    RESOURCE_NOT_EXIST_404("记录不存在");

    public String desc;

    @Override
    public String toString() {
        return "ErrorDesc{" +
                "desc='" + desc + '\'' +
                '}';
    }

    public String getdesc(){
        return desc;
    }

    ErrorDesc(String desc) {
        this.desc = desc;
    }
}
