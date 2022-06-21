package com.example.openbank.service;

import com.example.openbank.dao.PayerDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PayerService {
    public void insertPayer(PayerDao payerDao)throws EntpayException;

    public List<PayerDao> queryAll()throws EntpayException;

    public PayerDao queryByPayerId(String payer_id)throws EntpayException;

    public PayerDao queryEntIdByOutTransferId(String out_transfer_id)throws EntpayException;

    public PayerDao queryEntIdByOutBatchTransferId(String out_batch_transfer_id)throws EntpayException;
}
