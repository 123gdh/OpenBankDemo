package com.example.openbank.service;

import com.example.openbank.dao.OpenBankDao;
import com.example.openbank.vo.ApplicationStatusVerificationVo;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.OpenBankSignParam;

public interface OpenBankService {
    public void updateState(String state,String out_application_id)throws EntpayException;

    public void updateStateAndApplicationId(String state,String application_id,String out_application_id)throws EntpayException;

    public void updateEntAcctId(String state,String ent_acct_id,String out_application_id);

    public ApplicationStatusVerificationVo stateVerification(OpenBankSignParam openBankSP)throws EntpayException;

    public OpenBankDao queryOpenBank(String bank_account_name,String bank_account_number,String bank_abbreviation)throws EntpayException;

    public OpenBankDao queryOpenBankByOutApplicationId(String out_application_id)throws EntpayException;

    public void insertOpenBnak(OpenBankDao productOpenDao) throws EntpayException;
}
