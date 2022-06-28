package com.example.openbank.service;

import com.example.openbank.dao.OpenBankBatchTransferDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface OpenBankBatchTransferService {
    void insertOpenBankBatchTransfer(OpenBankBatchTransferDao openBankBatchTransferDao)throws EntpayException;

    OpenBankBatchTransferDao queryOpenBankBatchTransferDao(String out_batch_transfer_id)throws EntpayException;

    void updateStatusByOutId(String batch_transfer_status,String out_batch_transfer_id)throws EntpayException;

    void updateBatchTransferId(String batch_transfer_id,String out_batch_transfer_id)throws EntpayException;
}
