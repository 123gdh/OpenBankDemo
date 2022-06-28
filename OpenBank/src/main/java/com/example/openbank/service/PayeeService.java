package com.example.openbank.service;

import com.example.openbank.dao.PayeeDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PayeeService {
    List<PayeeDao> queryAll()throws EntpayException;

    PayeeDao queryByOutPayeeId(String out_payee_id)throws EntpayException;

    void insertPayee(PayeeDao payeeDao)throws EntpayException;
}
