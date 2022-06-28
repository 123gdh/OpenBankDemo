package com.example.openbank.service;


import com.example.openbank.dao.ProductOpenDao;
import com.example.openbank.vo.ApplicationStatusVerificationVo;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.ProductApplicationParam;

import java.sql.Date;
import java.util.List;

public interface GoProductService {

    void insertProduct(ProductOpenDao productOpenDao)throws EntpayException;

    ProductOpenDao queryProduct(String unified_social_credit_code,String product_name)throws EntpayException;

    List<ProductOpenDao> queryOverdueApplication(Date date)throws EntpayException;

    ProductOpenDao queryByOutRequestNo(String out_request_no)throws EntpayException;

    ApplicationStatusVerificationVo stateVerification(ProductApplicationParam param) throws EntpayException;

    void updateStateByUnifiedPname(String status,String unified_social_credit_code,String product_name)throws EntpayException;

    void updateStateAndRequestNo(String status,String request_no,String unified_social_credit_code,String product_name)throws EntpayException;

    void updateStateByOutRequestNo(String status,String out_request_no)throws EntpayException;
}
