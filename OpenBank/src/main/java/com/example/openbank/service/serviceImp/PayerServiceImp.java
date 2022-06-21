package com.example.openbank.service.serviceImp;

import com.example.openbank.dao.PayerDao;
import com.example.openbank.mapper.PayerMapper;
import com.example.openbank.service.PayerService;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.OpenBankTransferPayerParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PayerServiceImp implements PayerService {
    @Autowired
    private PayerMapper payerMapper;

    @Override
    public void insertPayer(PayerDao payerDao) throws EntpayException {
        try {
            payerMapper.insertPayer(payerDao);
        } catch (Exception e) {
            log.error("转账付款方：存储付款方信息异常"+e.getMessage());
            throw new EntpayException("转账：存储付款方信息异常"+e.getMessage(),e);
        }
    }

    @Override
    public List<PayerDao> queryAll() throws EntpayException {
        try {
            return payerMapper.queryAll();
        } catch (Exception e) {
            log.error("转账付款方：查询所有付款方信息异常"+e.getMessage());
            throw new EntpayException("转账：查询所有付款方信息异"+e.getMessage(),e);
        }
    }

    @Override
    public PayerDao queryByPayerId(String payer_id) throws EntpayException {
        try {
            return payerMapper.queryByPayerId(payer_id);
        } catch (Exception e) {
            log.error("转账付款方：根据付款方id查询单条付款方详情异常"+e.getMessage());
            throw new EntpayException("转账：根据付款方id查询单条付款方详情异常"+e.getMessage(),e);
        }
    }

    @Override
    public PayerDao queryEntIdByOutTransferId(String out_transfer_id) throws EntpayException {
        try {
            return payerMapper.queryEntIdByOutTransferId(out_transfer_id);
        } catch (Exception e) {
            log.error("转账付款方：根据平台转账单号查询单条付款方详情异常"+e.getMessage());
            throw new EntpayException("转账：根据平台转账单号查询单条付款方详情异常"+e.getMessage(),e);
        }
    }

    @Override
    public PayerDao queryEntIdByOutBatchTransferId(String out_batch_transfer_id) throws EntpayException {
        try {
            return payerMapper.queryEntIdByOutBatchTransferId(out_batch_transfer_id);
        } catch (Exception e) {
            log.error("转账付款方：根据平台批量转账批次号查询单条付款方详情异常"+e.getMessage());
            throw new EntpayException("转账：根据平台批量转账批次号查询单条付款方详情异常"+e.getMessage(),e);
        }
    }

    public String createAndInsertPayer(OpenBankTransferPayerParam payer)throws EntpayException{
        try {
            PayerDao payerDao = new PayerDao();
            payerDao.setEnt_id(payer.getEntId());
            payerDao.setEnt_name(payer.getEntName());
            payerDao.setEnt_acct_id(payer.getEntAcctId());
            payerDao.setBank_account_number_last4(payer.getBankAccountNumberLast4());
            String s = UUID.randomUUID().toString();
            payerDao.setPayer_id(s);
            insertPayer(payerDao);
            return s;
        } catch (EntpayException e) {
            log.error("转账：构建并存储付款方信息异常: {}",e.getMessage());
            throw new EntpayException("转账：构建并存储付款方信息异常",e);
        }
    }
}
