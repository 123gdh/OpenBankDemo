package com.example.openbank.result;

import com.example.openbank.enums.Errordescription;
import com.example.openbank.enums.Msg;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonTypeName("result")
public class Result<T> {
    //code为状态码
    @JsonProperty("retcode")
    private Integer retcode;

    //msg为提示信息
    @JsonProperty("retmsg")
    private Msg retmsg;

    //data为返回的数据
    @JsonProperty("data")
    private T data;

    @JsonProperty("errordescription")
    //错误详细信息
    private Errordescription errordescription;

    public Result(Integer retcode, Msg retmsg, T data) {
        this.retcode = retcode;
        this.retmsg = retmsg;
        this.data = data;
    }

    public Result(Integer retcode, Msg retmsg, T data, Errordescription errordescription) {
        this.retcode = retcode;
        this.retmsg = retmsg;
        this.data = data;
        this.errordescription = errordescription;
    }

}
