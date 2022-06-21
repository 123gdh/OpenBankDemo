package com.example.openbank.service;

import com.example.openbank.dao.OpenBankBatchTransferTimingDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OpenBankBatchTransferTimingService {
    public List<OpenBankBatchTransferTimingDao> queryAll()throws EntpayException;

    public void insertOpenBankBatchTransferTiming(OpenBankBatchTransferTimingDao openBankBatchTransferTimingDao)throws EntpayException;

    public void deleteOpenBankBatchTransferTiming(String out_batch_transfer_id)throws EntpayException;
}
