package com.example.openbank.service.serviceImp;

import com.example.openbank.dao.OpenBankBatchTransferTimingDao;
import com.example.openbank.mapper.OpenBankBatchTransferTimingMapper;
import com.example.openbank.service.OpenBankBatchTransferTimingService;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OpenBankBatchTransferTimingServiceImp implements OpenBankBatchTransferTimingService {
    @Autowired
    private OpenBankBatchTransferTimingMapper openBankBatchTransferTimingMapper;
    @Override
    public List<OpenBankBatchTransferTimingDao> queryAll() throws EntpayException {
        try {
            return openBankBatchTransferTimingMapper.queryAll();
        } catch (Exception e) {
            log.error("批量转账定时查询：查询所有需定时查询批量转账异常: {}",e.getMessage());
            throw new EntpayException("批量转账定时查询：查询所有需定时查询批量转账异常:",e);
        }
    }

    @Override
    public void insertOpenBankBatchTransferTiming(OpenBankBatchTransferTimingDao openBankBatchTransferTimingDao) throws EntpayException {
        try {
            openBankBatchTransferTimingMapper.insertOpenBankBatchTransferTiming(openBankBatchTransferTimingDao);
        } catch (Exception e) {
            log.error("批量转账定时查询：存储需定时查询的批量转账详情异常: {}",e.getMessage());
            throw new EntpayException("批量转账定时查询：存储需定时查询的批量转账详情异常:",e);
        }
    }

    @Override
    public void deleteOpenBankBatchTransferTiming(String out_batch_transfer_id) throws EntpayException {
        try {
            openBankBatchTransferTimingMapper.deleteOpenBankBatchTransferTiming(out_batch_transfer_id);
        } catch (Exception e) {
            log.error("批量转账定时查询，根据指定平台批量转账批次号移除定时查询数据: {}",e.getMessage());
            throw new EntpayException("批量转账定时查询：根据指定平台批量转账批次号移除定时查询数据:",e);
        }
    }
}
