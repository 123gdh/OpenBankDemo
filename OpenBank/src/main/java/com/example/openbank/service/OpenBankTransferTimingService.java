package com.example.openbank.service;

import com.example.openbank.dao.OpenBankTransferTimingDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OpenBankTransferTimingService {
    public List<OpenBankTransferTimingDao> queryAll()throws EntpayException;

    public OpenBankTransferTimingDao queryByOutId(String out_transfer_id)throws EntpayException;

    public void deleteByOutId(String out_transfer_id)throws EntpayException;

    public void insertOpenBankTransferTiming(OpenBankTransferTimingDao openBankTransferTimingDao)throws EntpayException ;

}
