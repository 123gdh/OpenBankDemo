package com.example.openbank.service;

import com.example.openbank.dao.ProductTimingDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;

import java.util.List;

public interface ProductTimingService {
    public void insertProductTiming(ProductTimingDao productTimingDao)throws EntpayException;

    public List<ProductTimingDao> queryAll()throws EntpayException;

    public ProductTimingDao queryByOutRequestNo(String out_request_no)throws EntpayException;

    public void deleteProductTiming(String out_request_no)throws EntpayException;
}
