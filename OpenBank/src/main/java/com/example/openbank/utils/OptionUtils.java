package com.example.openbank.utils;

import com.example.openbank.controller.GoProduct;
import com.tenpay.business.entpay.sdk.api.Enterprise;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.Certificate;
import com.tenpay.business.entpay.sdk.net.RequestOptions;
import org.springframework.stereotype.Component;

@Component
public class OptionUtils {
    /**
     * 获取软证书
     * @param ent_id
     * @return
     * @throws EntpayException
     */
    public static RequestOptions getOp(String ent_id) throws EntpayException {
        RequestOptions requestOptions = GoProduct.requestOptionsMap.get(ent_id);
        if (requestOptions != null){
            return requestOptions;
        }else{
            Certificate certificate = Enterprise.retrieveCertificate(ent_id); //ent_id
            RequestOptions options = RequestOptions.getInstance()
                    .initOpenBank(certificate.getEntId(), certificate.getSerialNumber(), certificate.getEncryptedPrivateKey());
            GoProduct.requestOptionsMap.put(ent_id,options);
            return options;
        }
    }
}
