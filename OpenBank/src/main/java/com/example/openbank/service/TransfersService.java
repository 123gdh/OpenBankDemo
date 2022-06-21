package com.example.openbank.service;

import com.example.openbank.dao.TransfersDao;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TransfersService {
    public void insertTransfers(TransfersDao transfersDao)throws EntpayException;

    public List<TransfersDao> queryByTransferId(String transfer_id)throws EntpayException;

    public void updateTransfersStatus(String transfer_status, String out_transfer_detail_id)throws  EntpayException;

}
