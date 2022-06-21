package com.example.openbank.service;

import com.example.openbank.dao.OpenBankBatchTransferDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface OpenBankBatchTransferService {
    public void insertOpenBankBatchTransfer(OpenBankBatchTransferDao openBankBatchTransferDao)throws EntpayException;

    public OpenBankBatchTransferDao queryOpenBankBatchTransferDao(String out_batch_transfer_id)throws EntpayException;

    public void updateStatusByOutId(String batch_transfer_status,String out_batch_transfer_id)throws EntpayException;

    public void updateBatchTransferId(String batch_transfer_id,String out_batch_transfer_id)throws EntpayException;
}
