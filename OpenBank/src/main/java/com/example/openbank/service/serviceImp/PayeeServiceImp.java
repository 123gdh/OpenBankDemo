package com.example.openbank.service.serviceImp;

import com.example.openbank.dao.PayeeDao;
import com.example.openbank.mapper.PayeeMapper;
import com.example.openbank.service.PayeeService;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.OpenBankTransferPayeeParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PayeeServiceImp implements PayeeService {
    @Autowired
    private PayeeMapper payeeMapper;
    @Override
    public List<PayeeDao> queryAll() throws EntpayException {
        try {
            return payeeMapper.queryAll();
        } catch (Exception e) {
            log.info("转账收款方：查询所有转账收款方数据异常"+e.getMessage());
            throw new EntpayException("转账收款方：查询所有转账收款方数据异常"+e.getMessage());
        }
    }

    @Override
    public PayeeDao queryByoutPayeeId(String out_payee_id) throws EntpayException {
        try {
            return payeeMapper.queryByoutPayeeId(out_payee_id);
        } catch (Exception e) {
            log.info("转账收款方：通过平台收款方id查询转账收款方数据异常"+e.getMessage());
            throw new EntpayException("转账收款方：通过平台收款方id查询转账收款方数据异常"+e.getMessage());
        }
    }

    @Override
    public void insertPayee(PayeeDao payeeDao) throws EntpayException {
        try {
            payeeMapper.insertPayee(payeeDao);
        } catch (Exception e) {
            log.info("转账收款方：存储转账收款方数据异常"+e.getMessage());
            throw new EntpayException("转账收款方：存储转账收款方数据异常"+e.getMessage());
        }
    }

    public String createAndInsertPayee(OpenBankTransferPayeeParam payee) throws EntpayException {
        try {
            PayeeDao payeeDao = new PayeeDao();
            payeeDao.setBank_account_name(payee.getBankAccountName());
            payeeDao.setBank_account_number(payee.getBankAccountNumber());
            payeeDao.setBank_branch_id(payee.getBankBranchId());
            payeeDao.setBank_branch_name(payeeDao.getBank_branch_name());
            String s = UUID.randomUUID().toString();
            payeeDao.setOut_payee_id(s);
            insertPayee(payeeDao);
            return s;
        } catch (EntpayException e) {
            log.info("转账收款方：构建并存储转账收款方数据异常: {}",e.getMessage());
            throw new EntpayException("转账收款方：构建并存储转账收款方数据异常",e);
        }
    }
}
