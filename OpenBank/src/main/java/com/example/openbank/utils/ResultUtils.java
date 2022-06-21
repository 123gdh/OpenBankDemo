package com.example.openbank.utils;


import com.example.openbank.enums.Errordescription;
import com.example.openbank.enums.Msg;
import com.example.openbank.enums.ResponseStatus;
import com.example.openbank.result.Result;

public class ResultUtils {
    /**
     * 处理成功
     * @param code 返回状态
     * @param msg  返回描述
     * @param data 返回对象
     */
    public static Result success(Integer code, Msg msg, Object data){
       return new Result(code,msg,data);
    }

    /**
     * 处理失败
     * @param code  返回状态
     * @param msg   返回描述
     * @param data  返回对象
     */
    public static Result fail(Integer code, Msg msg, Object data, Errordescription errordescription){
        return new Result(code,msg,data,errordescription);
    }

}
