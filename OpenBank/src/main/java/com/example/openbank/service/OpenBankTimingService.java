package com.example.openbank.service;

import com.example.openbank.dao.OpenBankScheduledDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;

import java.util.List;

public interface OpenBankTimingService {
    public List<OpenBankScheduledDao> queryAll()throws EntpayException;

    public OpenBankScheduledDao queryByOutId(String out_application_id)throws EntpayException;

    public void insertOpenBankScheduledDao(OpenBankScheduledDao openBankScheduledDao)throws EntpayException;

    public void deleteOpenBankScheduledDao(String out_application_id)throws EntpayException;

}
