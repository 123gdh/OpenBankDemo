package com.example.openbank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonTypeName("error")
public class ErrorMessage {
    //错误描述
    @JsonProperty("desc")
    private String desc;
    //错误类型
    @JsonProperty("code")
    private String code;
    //错误详细说明
    @JsonProperty("detail")
    private Object detail;

    public ErrorMessage(String desc, String code, Object detail) {
        this.desc = desc;
        this.code = code;
        this.detail = detail;
    }

    public ErrorMessage() {
        this.desc = "错误描述信息";
        this.code =  "错误类型信息";
        this.detail = "错误详细对象(具体错误信息后续可优化)";
    }
}
