package com.example.openbank.utils;

import com.tenpay.business.entpay.sdk.common.ApiError;
import com.tenpay.business.entpay.sdk.exception.ApiException;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class CreateApiException {
    public static ApiException createApiException(EntpayException e, String desc, String code, Object detail){
            ApiException apiException = new ApiException();
        BeanUtils.copyProperties(e,apiException);
            ApiError apiError = new ApiError();
            apiError.setDesc(desc);
            apiError.setCode(code);
            apiError.setDetail(detail);
            apiException.setError(apiError);
            return apiException;
    }
}
