package com.example.openbank.enums;

import com.example.openbank.vo.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tenpay.business.entpay.sdk.common.ApiError;

@JsonPropertyOrder({"trace_id", "uri", "uri_pattern","error"})
@JsonTypeName("Errordescription")
public enum Errordescription {
    @JsonProperty("offlineexception")
    OFFLINEEXCEPTION("xxx","/v3/xxx","/v3/xxx",new ErrorMessage()),
    @JsonProperty("registerotfinished")
    REGISTERNOTFINISHED("xxx","/v3/xxx","/v3/xxx",new ErrorMessage("异常信息","异常信息","请完成上一个申请流程（REGISTER_NOT_FINISHED）")),
    @JsonProperty("signprocessing")
    SIGNPROCESSING("xxx","/v3/xxx","/v3/xxx",new ErrorMessage("异常信息","异常信息","该账户存在签约中的流程，请完成上一个流程（SIGN_PROCESSING）"));

    //链路ID
    @JsonProperty("trace_id")
    private String trace_id;
    //统一资源标志符
    @JsonProperty("uri")
    private String uri;
    //统一资源标志符范式
    @JsonProperty("uri_pattern")
    private String uri_pattern;
    //错误信息
    @JsonProperty("error")
    private ErrorMessage error;

    @JsonProperty("apierror")
    private ApiError apiError;

    Errordescription(String trace_id, String uri, String uri_pattern, ErrorMessage error) {
        this.trace_id = trace_id;
        this.uri = uri;
        this.uri_pattern = uri_pattern;
        this.error = error;
    }

    @Override
    public String toString() {
        return "Errordescription{" +
                "trace_id='" + trace_id + '\'' +
                ", uri='" + uri + '\'' +
                ", uri_pattern='" + uri_pattern + '\'' +
                ", error=" + error +
                '}';
    }


}
