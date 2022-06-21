package com.example.openbank.service.serviceImp;

import com.example.openbank.dao.OpenBankTransferDao;
import com.example.openbank.dao.OpenBankTransferTimingDao;
import com.example.openbank.mapper.OpenBankTransferTimingMapper;
import com.example.openbank.service.OpenBankTransferService;
import com.example.openbank.service.OpenBankTransferTimingService;
import com.tenpay.business.entpay.sdk.api.OpenBankTransfer;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.OpenBankTransferParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OpenBankTransferTimingServiceImp implements OpenBankTransferTimingService {
    @Autowired
    private OpenBankTransferTimingMapper openBankTransferTimingMapper;

    @Override
    public List<OpenBankTransferTimingDao> queryAll() throws EntpayException {
        try {
            return openBankTransferTimingMapper.queryAll();
        } catch (Exception e) {
            log.error("单笔转账定时查询：获取所有需定时查询的单笔申请单异常: {}",e.getMessage());
            throw new EntpayException("单笔转账定时查询：获取所有需定时查询的单笔申请单异常",e);
        }
    }

    @Override
    public OpenBankTransferTimingDao queryByOutId(String out_transfer_id) throws EntpayException {
        try {
            return openBankTransferTimingMapper.queryByOutId(out_transfer_id);
        } catch (Exception e) {
            log.error("单笔转账定时查询：获取单条需定时查询的单笔申请单异常: {}",e.getMessage());
            throw new EntpayException("单笔转账定时查询：获取单条需定时查询的单笔申请单异常",e);
        }
    }

    @Override
    public void deleteByOutId(String out_transfer_id) throws EntpayException {
        try {
            openBankTransferTimingMapper.deleteByOutId(out_transfer_id);
        } catch (Exception e) {
            log.error("单笔转账定时查询：根据平台转账单号删除定时查询数据单异常: {}",e.getMessage());
            throw new EntpayException("单笔转账定时查询：根据平台转账单号删除定时查询数据单异常",e);
        }
    }

    @Override
    public void insertOpenBankTransferTiming(OpenBankTransferTimingDao openBankTransferTimingDao)throws EntpayException {
        try {
            openBankTransferTimingMapper.insertOpenBankTransferTiming(openBankTransferTimingDao);
        } catch (Exception e) {
            log.error("单笔转账定时查询：存储需定时查询单笔转账单异常: {}",e.getMessage());
            throw new EntpayException("单笔转账定时查询：存储需定时查询单笔转账单异常",e);
        }
    }


    public void retryUpdete(OpenBankTransferParam openBankTransferParam)throws EntpayException {
        int len = 3;
        for (int i = 1; i <= len; i++) {
            try {
                OpenBankTransferTimingDao openBankTransferTimingDao = queryByOutId(openBankTransferParam.getOutTransferId());
                if (openBankTransferTimingDao != null) {
                    log.info("单笔转账定时查询：存储平台转账单号为" + openBankTransferParam.getOutTransferId() + "的转账单，重试存储：成功");
                    continue;
                }
                insertOpenBankTransferTiming(new OpenBankTransferTimingDao(openBankTransferParam.getOutTransferId(),openBankTransferParam.getPayer().getEntId()));
                i--;
            } catch (EntpayException entpayException) {
                log.error("单笔转账定时查询：存储平台转账单号为"+openBankTransferParam.getOutTransferId()+"的转账单，尝试重试存储，第"+i+"次重试: 失败 :{}",entpayException.getMessage());
                if (i == len){
                    log.error("单笔转账定时查询：存储平台转账单号为"+openBankTransferParam.getOutTransferId()+"的转账单，重试存储：失败 :{}",entpayException.getMessage());
                    throw new EntpayException("单笔转账定时查询：存储平台转账单号为"+openBankTransferParam.getOutTransferId()+"的转账单异常，重试存储：失败",entpayException);
                }
            }
        }
    }
}
