package com.example.openbank.service;

import com.example.openbank.dao.PayerDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PayerService {
    void insertPayer(PayerDao payerDao)throws EntpayException;

    List<PayerDao> queryAll()throws EntpayException;

    PayerDao queryByPayerId(String payer_id)throws EntpayException;

    PayerDao queryEntIdByOutTransferId(String out_transfer_id)throws EntpayException;

    PayerDao queryEntIdByOutBatchTransferId(String out_batch_transfer_id)throws EntpayException;
}
