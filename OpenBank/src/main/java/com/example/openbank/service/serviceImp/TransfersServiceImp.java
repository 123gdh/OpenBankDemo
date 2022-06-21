package com.example.openbank.service.serviceImp;

import com.example.openbank.dao.TransfersDao;
import com.example.openbank.mapper.TransfersMapper;
import com.example.openbank.pojo.OpenBankBatchTransferParamAndTransferId;
import com.example.openbank.service.TransfersService;
import com.example.openbank.utils.CreateOutId;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.OpenBankBatchTransferDetail;
import com.tenpay.business.entpay.sdk.model.OpenBankBatchTransferDetailParam;
import com.tenpay.business.entpay.sdk.model.OpenBankBatchTransferParam;
import com.tenpay.business.entpay.sdk.model.OpenBankTransferPayeeParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TransfersServiceImp implements TransfersService {
    @Autowired
    private TransfersMapper transfersMapper;
    @Autowired
    private PayeeServiceImp payeeServiceImp;

    @Override
    public void insertTransfers(TransfersDao transfersDao) throws EntpayException {
        try {
            transfersMapper.insertTransfers(transfersDao);
        } catch (Exception e) {
            log.error("批量转账明细：存储明细详情异常: {}",e.getMessage());
            throw new EntpayException("批量转账明细：存储明细详情异常",e);
        }
    }

    @Override
    public List<TransfersDao> queryByTransferId(String transfer_id) throws EntpayException {
        try {
            return transfersMapper.queryByTransferId(transfer_id);
        } catch (Exception e) {
            log.error("批量转账明细：根据批量转账细明id查询转账明细异常: {}",e.getMessage());
            throw new EntpayException("批量转账明细：根据批量转账细明id查询转账明细异常",e);
        }
    }

    @Override
    public void updateTransfersStatus(String transfer_status, String out_transfer_detail_id) throws EntpayException {
        try {
            transfersMapper.updateTransfersStatus(transfer_status,out_transfer_detail_id);
        } catch (Exception e) {
            log.error("批量转账明细：更新批量转账明细状态异常: {}",e.getMessage());
            throw new EntpayException("批量转账明细：更新批量转账明细状态异常",e);
        }
    }

    @Transactional(rollbackFor = EntpayException.class)
    public OpenBankBatchTransferParamAndTransferId createTransfer(OpenBankBatchTransferParam openBankBatchTransferParam) throws EntpayException {
        List<OpenBankBatchTransferDetailParam> transfers = openBankBatchTransferParam.getTransfers();
        String transfer_id;
        try {
            transfer_id = UUID.randomUUID().toString();
            if (transfers != null){
                for (OpenBankBatchTransferDetailParam transfer : transfers) {
                    OpenBankTransferPayeeParam payee = transfer.getPayee();
                    String payeeId = payeeServiceImp.createAndInsertPayee(payee);
                    String out_transfer_detail_id = CreateOutId.createId();
                    transfer.setOutTransferDetailId(out_transfer_detail_id);
                    TransfersDao transfersDao = new TransfersDao();
                    transfersDao.setTransfer_id(transfer_id);
                    transfersDao.setOut_transfer_detail_id(out_transfer_detail_id);
                    transfersDao.setPayee_id(payeeId);
                    transfersDao.setAmount(transfer.getAmount());
                    transfersDao.setMemo(transfer.getMemo());
                    transfersDao.setTransfer_status("初始化");
                    insertTransfers(transfersDao);
                }
            }
        } catch (EntpayException e) {
            log.error("批量转账明细：存储批量转账明细详情异常: {}",e.getMessage());
            throw new EntpayException("批量转账明细：存储批量转账明细详情异常",e);
        }
        return new OpenBankBatchTransferParamAndTransferId(openBankBatchTransferParam,transfer_id);
    }
}
