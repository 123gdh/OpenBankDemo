package com.example.openbank.service.serviceImp;

import com.example.openbank.dao.OpenBankTransferDao;
import com.example.openbank.dao.OpenBankTransferTimingDao;
import com.example.openbank.dao.ProductOpenDao;
import com.example.openbank.mapper.OpenBankTransferMapper;
import com.example.openbank.service.OpenBankTransferService;
import com.example.openbank.service.OpenBankTransferTimingService;
import com.example.openbank.utils.OptionUtils;
import com.tenpay.business.entpay.sdk.api.OpenBankTransfer;
import com.tenpay.business.entpay.sdk.api.ProductApplication;
import com.tenpay.business.entpay.sdk.exception.ApiException;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.*;
import com.tenpay.business.entpay.sdk.net.RequestOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OpenBankTransferServiceImp implements OpenBankTransferService {
    @Autowired
    private OpenBankTransferMapper openBankTransferMapper;

    @Autowired
    private OpenBankTransferTimingService openBankTransferTimingService;

    @Autowired
    private OpenBankTransferTimingServiceImp openBankTransferTimingServiceImp;

    @Autowired
    private PayerServiceImp payerServiceImp;

    @Autowired
    private PayeeServiceImp payeeServiceImp;

    //构造唯一OutTransferTd使用
    private static ConcurrentHashMap<String,Integer> getOutTransferTd = new ConcurrentHashMap<String,Integer>();

    @Override
    public OpenBankTransferDao queryByOutId(String out_transfer_id) throws EntpayException {
        try {
            return openBankTransferMapper.queryByOutId(out_transfer_id);
        } catch (Exception e) {
            log.error("单笔转账：通过平台转账单号查询转账详情失败: {}",e.getMessage());
            throw new EntpayException("单笔转账：通过平台转账单号查询转账详情失败",e);
        }
    }

    @Override
    public void insertOpenBankTransfer(OpenBankTransferDao openBankTransferDao) throws EntpayException{
        try {
            openBankTransferMapper.insertOpenBankTransfer(openBankTransferDao);
        } catch (Exception e) {
            log.error("单笔转账：存储转账详情异常"+e.getMessage());
            throw new EntpayException("单笔转账：存储转账详情异常"+e.getMessage(),e);
        }
    }

    @Override
    public void updateStatusByOutId(String transfer_status, String out_transfer_id) throws EntpayException {
        try {
            openBankTransferMapper.updateStatusByOutId(transfer_status,out_transfer_id);
        } catch (Exception e) {
            log.error("单笔转账：通过平台转账单号修改转账状态异常: {}",e.getMessage());
            throw new EntpayException("单笔转账：通过平台转账单号修改转账状态异常",e);
        }
    }

    public String createOutTransferTd(){
        Integer out_transfer_id = getOutTransferTd.get("out_transfer_id");
        if (out_transfer_id == null){
            out_transfer_id = 0;
        }
        getOutTransferTd.put("out_transfer_id",out_transfer_id++);
        return String.valueOf(System.currentTimeMillis())+String.valueOf(getOutTransferTd.get("out_transfer_id"));
    }
    public String insertOpenBankTransfer(OpenBankTransferParam openBankTransferParam,String payer_id,String payee_id) throws EntpayException {
        try {
            OpenBankTransferDao openBankTransferDao = new OpenBankTransferDao();
            String outTransferTd = createOutTransferTd();
            openBankTransferDao.setOut_transfer_id(outTransferTd);
            openBankTransferDao.setPayee_id(payee_id);
            openBankTransferDao.setPayer_id(payer_id);
            openBankTransferDao.setTransfer_status("初始化");
            openBankTransferDao.setAmount(openBankTransferParam.getAmount());
            openBankTransferDao.setCurrency(openBankTransferParam.getCurrency().getDesc());
            openBankTransferDao.setMemo(openBankTransferParam.getMemo());
            if (openBankTransferParam.getGoods() != null){
                openBankTransferDao.setGoods_name(openBankTransferParam.getGoods().getGoodsName());
                openBankTransferDao.setGoods_detail(openBankTransferParam.getGoods().getGoodsDetail());
            }
            openBankTransferDao.setAttachment(openBankTransferParam.getAttachment());
            insertOpenBankTransfer(openBankTransferDao);
            return outTransferTd;
        } catch (EntpayException e) {
            log.error("单笔转账：存储单笔转账申请单异常: {}",e.getMessage());
            throw new EntpayException("单笔转账：存储单笔转账申请单异常",e);
        }
    }

    @Transactional(rollbackFor = EntpayException.class)
    public String createAndInsertOpenBankTransfer(OpenBankTransferParam openBankTransferParam) throws EntpayException {
        try {
            String payer_id = payerServiceImp.createAndInsertPayer(openBankTransferParam.getPayer());
            String payee_id = payeeServiceImp.createAndInsertPayee(openBankTransferParam.getPayee());
            return insertOpenBankTransfer(openBankTransferParam, payer_id, payee_id);
        } catch (EntpayException e) {
            throw new EntpayException(e);
        }
    }

    @Transactional(rollbackFor = EntpayException.class)
    public OpenBankTransfer createTransfer(OpenBankTransferParam openBankTransferParam) throws EntpayException {
        try {
            openBankTransferTimingService.insertOpenBankTransferTiming(new OpenBankTransferTimingDao(openBankTransferParam.getOutTransferId(),openBankTransferParam.getPayer().getEntId()));
        } catch (EntpayException e) {
            try {
                openBankTransferTimingServiceImp.retryUpdete(openBankTransferParam);
            } catch (Exception exception) {
                log.error("单笔转账定时查询：异常重试存储需定时查询单笔转账单失败: {}",e.getMessage());
                throw new EntpayException("单笔转账定时查询：异常重试存储需定时查询单笔转账单失败: {}",e);
            }
        }
        String hostAddress;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取本地ip异常"+e.getMessage());
            hostAddress = "localhost";
        }
        try {
            RequestOptions requestOptions = OptionUtils.getOp(openBankTransferParam.getPayer().getEntId());
            OpenBankTransferPayerParam payer = openBankTransferParam.getPayer();
            OpenBankTransferPayeeParam payee = openBankTransferParam.getPayee();
            String outTransferId = openBankTransferParam.getOutTransferId();
            CurrencyEnum currency = openBankTransferParam.getCurrency();
            ServerNotifyUrl notifyUrl1 = openBankTransferParam.getNotifyUrl();
            ServerNotifyUrl notifyUrl = ServerNotifyUrl.builder()
                    .serverNotifyUrl("http://10.43.26.46:8080/Callback/transfers")
                    .build();
            OpenBankTransferParam openBankTransferP = OpenBankTransferParam.builder()
                    .outTransferId(outTransferId) // 平台转账单号
                    .amount(1000L) // 转账金额，单位：分
                    .payer(payer)
                    .payee(payee)
                    .notifyUrl(notifyUrl)
                    .currency(openBankTransferParam.getCurrency())
                    .build();
            return OpenBankTransfer.create(openBankTransferP, requestOptions);
        } catch (EntpayException e) {
            log.error("单笔转账：发起单笔转账异常:{}",e.getMessage());
            throw new EntpayException("单笔转账：发起单笔转账异常",e);
        }
    }

    public void retryUpdete(OpenBankTransferParam openBankTransferParam)throws EntpayException{
        int len = 3;
        for (int i = 1; i <= len; i++) {
            try {
                OpenBankTransferDao openBankTransferDao = queryByOutId(openBankTransferParam.getOutTransferId());
                if (openBankTransferDao != null) {
                    if (!openBankTransferDao.getTransfer_status().equals("初始化")) {
                        log.info("申请产品开通：申请单号为" + openBankTransferParam.getOutTransferId() + "的申请单修改申请产品开通状态为PROCESSING，重试修改状态：成功");
                        continue;
                    }
                }
                updateStatusByOutId(OpenBankTransfer.TransferStatusEnum.PROCESSING.getDesc(),openBankTransferParam.getOutTransferId());
                i--;
            } catch (EntpayException entpayException) {
                log.error("申请产品开通：申请单号为"+openBankTransferParam.getOutTransferId()+"的申请单修改申请产品开通状态为PROCESSING发送异常，尝试重试修改状态，第"+i+"次重试: 失败 :{}",entpayException.getMessage());
                if (i == len){
                    log.error("申请产品开通：申请单号为"+openBankTransferParam.getOutTransferId()+"的申请单修改申请产品开通状态为PROCESSING发送异常，重试修改状态：失败 :{}",entpayException.getMessage());
                    throw new EntpayException("申请产品开通：申请单号为"+openBankTransferParam.getOutTransferId()+"的申请单修改申请产品开通状态为PROCESSING发送异常，重试修改状态：失败",entpayException);
                }
            }
        }
    }

}
