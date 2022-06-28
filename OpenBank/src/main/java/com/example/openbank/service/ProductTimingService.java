package com.example.openbank.service;

import com.example.openbank.dao.ProductTimingDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;

import java.util.List;

public interface ProductTimingService {
    void insertProductTiming(ProductTimingDao productTimingDao)throws EntpayException;

    List<ProductTimingDao> queryAll()throws EntpayException;

    ProductTimingDao queryByOutRequestNo(String out_request_no)throws EntpayException;

    void deleteProductTiming(String out_request_no)throws EntpayException;
}
