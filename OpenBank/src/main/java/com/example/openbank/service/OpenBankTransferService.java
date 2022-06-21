package com.example.openbank.service;

import com.example.openbank.dao.OpenBankTransferDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;

public interface OpenBankTransferService {
    public OpenBankTransferDao queryByOutId(String out_transfer_id) throws EntpayException;

    public void insertOpenBankTransfer(OpenBankTransferDao openBankTransferDao)throws EntpayException;

    public void updateStatusByOutId(String transfer_status,String out_transfer_id)throws EntpayException;
}
