package com.example.openbank.service.serviceImp;

import com.example.openbank.dao.OpenBankBatchTransferDao;
import com.example.openbank.dao.OpenBankBatchTransferTimingDao;
import com.example.openbank.mapper.OpenBankBatchTransferMapper;
import com.example.openbank.mapper.OpenBankBatchTransferTimingMapper;
import com.example.openbank.pojo.OpenBankBatchTransferParamAndTransferId;
import com.example.openbank.service.OpenBankBatchTransferService;
import com.example.openbank.service.OpenBankBatchTransferTimingService;
import com.example.openbank.service.PayeeService;
import com.example.openbank.service.PayerService;
import com.example.openbank.utils.CreateOutId;
import com.example.openbank.utils.OptionUtils;
import com.tenpay.business.entpay.sdk.api.OpenBankBatchTransfer;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.*;
import com.tenpay.business.entpay.sdk.net.RequestOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OpenBankBatchTransferServiceImp implements OpenBankBatchTransferService {
    @Autowired
    private OpenBankBatchTransferMapper openBankBatchTransferMapper;
    @Autowired
    private PayerServiceImp payerServiceImp;
    @Autowired
    private TransfersServiceImp transfersServiceImp;
    @Autowired
    private OpenBankBatchTransferTimingService openBankBatchTransferTimingService;

    @Override
    public void insertOpenBankBatchTransfer(OpenBankBatchTransferDao openBankBatchTransferDao) throws EntpayException {
        try {
            openBankBatchTransferMapper.insertOpenBankBatchTransfer(openBankBatchTransferDao);
        } catch (Exception e) {
            log.error("批量转账：存储批量转账详情异常"+e.getMessage());
            throw new EntpayException("批量转账：存储批量转账详情异常"+e.getMessage(),e);
        }
    }

    @Override
    public OpenBankBatchTransferDao queryOpenBankBatchTransferDao(String out_batch_transfer_id) throws EntpayException {
        try {
            return openBankBatchTransferMapper.queryOpenBankBatchTransferDao(out_batch_transfer_id);
        } catch (Exception e) {
            log.error("批量转账：通过平台批量转账批次号select批量转账异常"+e.getMessage());
            throw new EntpayException("批量转账：通过平台批量转账批次号select批量转账异常"+e.getMessage(),e);
        }
    }

    @Override
    public void updateStatusByOutId(String batch_transfer_status, String out_batch_transfer_id) throws EntpayException {
        try {
            openBankBatchTransferMapper.updateStatusByOutId(batch_transfer_status,out_batch_transfer_id);
        } catch (Exception e) {
            log.error("批量转账：通过平台批量转账批次号update批量转账状态异常"+e.getMessage());
            throw new EntpayException("批量转账：通过平台批量转账批次号update批量转账状态异常"+e.getMessage(),e);
        }
    }

    @Override
    public void updateBatchTransferId(String batch_transfer_id, String out_batch_transfer_id) throws EntpayException {
        try {
            openBankBatchTransferMapper.updateBatchTransferId(batch_transfer_id,out_batch_transfer_id);
        } catch (Exception e) {
            log.error("批量转账：调用银企付批量转账接口后修改银企支付转账总单号异常: {}",e.getMessage());
            throw new EntpayException("批量转账：调用银企付批量转账接口后修改银企支付转账总单号异常",e);
        }
    }

    @Transactional(rollbackFor = EntpayException.class)
    public OpenBankBatchTransferParam createOpenBankBatchTransfer(OpenBankBatchTransferParam  openBankBatchTransferParam) throws EntpayException {
        try {
            String payerId = payerServiceImp.createAndInsertPayer(openBankBatchTransferParam.getPayer());
            OpenBankBatchTransferParamAndTransferId openBankBatchTransferParamAndTransferId = transfersServiceImp.createTransfer(openBankBatchTransferParam);
            return insertOpenBankBatchTransfer(openBankBatchTransferParamAndTransferId.getOpenBankBatchTransferParam(), payerId, openBankBatchTransferParamAndTransferId.getTransferId());
        } catch (EntpayException e) {
            log.error("批量转账：存储批量转账详情异常: {}",e.getMessage());
            throw new EntpayException("批量转账：存储批量转账详情异常:",e);
        }
    }

    public OpenBankBatchTransferParam insertOpenBankBatchTransfer(OpenBankBatchTransferParam  openBankBatchTransferParam,String payer_id,String transfer_id) throws EntpayException {
        OpenBankBatchTransferDao openBankBatchTransferDao = new OpenBankBatchTransferDao();
        openBankBatchTransferDao.setPayer_id(payer_id);
        openBankBatchTransferDao.setTransfers(transfer_id);
        String out_batch_transfer_id = CreateOutId.createId();
        openBankBatchTransferParam.setOutBatchTransferId(out_batch_transfer_id);
        openBankBatchTransferDao.setOut_batch_transfer_id(out_batch_transfer_id);
        openBankBatchTransferDao.setTotal_amount(openBankBatchTransferParam.getTotalAmount());
        openBankBatchTransferDao.setCurrency(openBankBatchTransferParam.getCurrency().getDesc());
        openBankBatchTransferDao.setTotal_num(openBankBatchTransferParam.getTotalNum());
        openBankBatchTransferDao.setBatch_transfer_status(OpenBankBatchTransfer.BatchTransferStatusEnum.INIT.getDesc());
        openBankBatchTransferDao.setBatch_memo(openBankBatchTransferParam.getBatchMemo());
        openBankBatchTransferDao.setAttachment(openBankBatchTransferParam.getAttachment());
        if (openBankBatchTransferParam.getGoods() != null){
            openBankBatchTransferDao.setGoods_name(openBankBatchTransferParam.getGoods().getGoodsName());
            openBankBatchTransferDao.setGoods_detail(openBankBatchTransferParam.getGoods().getGoodsDetail());
        }
        insertOpenBankBatchTransfer(openBankBatchTransferDao);
        return openBankBatchTransferParam;
    }

    @Transactional(rollbackFor =EntpayException.class)
    public OpenBankBatchTransfer create(OpenBankBatchTransferParam  openBankBatchTransferParam) throws EntpayException {
        String hostAddress;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("批量转账：获取本地IP异常");
            hostAddress = "localhost";
        }
        OpenBankBatchTransferTimingDao openBankBatchTransferTimingDao = new OpenBankBatchTransferTimingDao();
        openBankBatchTransferTimingDao.setOut_batch_transfer_id(openBankBatchTransferParam.getOutBatchTransferId());
        openBankBatchTransferTimingDao.setEnt_id(openBankBatchTransferParam.getPayer().getEntId());
        try {
            openBankBatchTransferTimingService.insertOpenBankBatchTransferTiming(openBankBatchTransferTimingDao);
        } catch (EntpayException e) {
            log.error("批量转账定时查询：存储需定时查询的批量转账详情异常: {}",e.getMessage());
            e.printStackTrace();
        }
        try {
            ServerNotifyUrl notifyUrl = ServerNotifyUrl.builder()
                    .serverNotifyUrl("http://10.43.26.46:8080/Callback/batch-transfers")
                    .build();
            OpenBankBatchTransferParam openBankBatchTransferPar = OpenBankBatchTransferParam.builder()
                    .outBatchTransferId(openBankBatchTransferParam.getOutBatchTransferId()) // 平台批量转账批次号
                    .totalAmount(openBankBatchTransferParam.getTotalAmount())
                    .totalNum(openBankBatchTransferParam.getTotalNum())
                    .transfers(openBankBatchTransferParam.getTransfers())
                    .payer(openBankBatchTransferParam.getPayer())
                    .notifyUrl(notifyUrl)
                    .currency(openBankBatchTransferParam.getCurrency())
                    .build();
            RequestOptions requestOptions = OptionUtils.getOp(openBankBatchTransferParam.getPayer().getEntId());
            return OpenBankBatchTransfer.create(openBankBatchTransferPar,requestOptions);
        } catch (EntpayException e) {
            log.error("批量转账：调用银企付批量转账接口异常: {}",e.getMessage());
            throw new EntpayException("批量转账：调用银企付批量转账接口异常",e);
        }
    }

    @Transactional(rollbackFor = EntpayException.class)
    public void updateStatusParam(List<OpenBankBatchTransferDetailParam> transfers,String outBatchTransferId,String status) throws EntpayException {
        try {
            updateStatusByOutId(status,outBatchTransferId);
            for (OpenBankBatchTransferDetailParam transfer : transfers) {
                String outTransferDetailId = transfer.getOutTransferDetailId();
                transfersServiceImp.updateTransfersStatus(status,outTransferDetailId);
            }
        } catch (EntpayException e) {
            log.error("批量转账：修改批量转账总状态及明细状态异常: {}",e.getMessage());
            throw new EntpayException("批量转账：修改批量转账总状态及明细状态异常",e);
        }
    }
    public void updateStatus(List<OpenBankBatchTransferDetail> transfers,String outBatchTransferId,String status) throws EntpayException {
        try {
            updateStatusByOutId(status,outBatchTransferId);
            for (OpenBankBatchTransferDetail transfer : transfers) {
                String outTransferDetailId = transfer.getOutTransferDetailId();
                transfersServiceImp.updateTransfersStatus(status,outTransferDetailId);
            }
        } catch (EntpayException e) {
            log.error("批量转账：修改批量转账总状态及明细状态异常: {}",e.getMessage());
            throw new EntpayException("批量转账：修改批量转账总状态及明细状态异常",e);
        }
    }

}
