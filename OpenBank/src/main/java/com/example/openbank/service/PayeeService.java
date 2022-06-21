package com.example.openbank.service;

import com.example.openbank.dao.PayeeDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PayeeService {
    public List<PayeeDao> queryAll()throws EntpayException;

    public PayeeDao queryByoutPayeeId(String out_payee_id)throws EntpayException;

    public void insertPayee(PayeeDao payeeDao)throws EntpayException;
}
