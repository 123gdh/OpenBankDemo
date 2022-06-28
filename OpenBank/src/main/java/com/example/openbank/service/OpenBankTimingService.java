package com.example.openbank.service;

import com.example.openbank.dao.OpenBankScheduledDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;

import java.util.List;

public interface OpenBankTimingService {
    List<OpenBankScheduledDao> queryAll()throws EntpayException;

    OpenBankScheduledDao queryByOutId(String out_application_id)throws EntpayException;

    void insertOpenBankScheduledDao(OpenBankScheduledDao openBankScheduledDao)throws EntpayException;

    void deleteOpenBankScheduledDao(String out_application_id)throws EntpayException;

}
