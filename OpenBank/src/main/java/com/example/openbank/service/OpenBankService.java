package com.example.openbank.service;

import com.example.openbank.dao.OpenBankDao;
import com.example.openbank.vo.ApplicationStatusVerificationVo;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.OpenBankSignParam;

public interface OpenBankService {
    void updateState(String state,String out_application_id)throws EntpayException;

    void updateStateAndApplicationId(String state,String application_id,String out_application_id)throws EntpayException;

    void updateEntAcctId(String state,String ent_acct_id,String out_application_id);

    ApplicationStatusVerificationVo stateVerification(OpenBankSignParam openBankSP)throws EntpayException;

    OpenBankDao queryOpenBank(String bank_account_name,String bank_account_number,String bank_abbreviation)throws EntpayException;

    OpenBankDao queryOpenBankByOutApplicationId(String out_application_id)throws EntpayException;

    void insertOpenBnak(OpenBankDao productOpenDao) throws EntpayException;
}
