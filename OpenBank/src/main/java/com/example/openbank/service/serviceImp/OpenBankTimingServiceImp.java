package com.example.openbank.service.serviceImp;

import com.example.openbank.dao.OpenBankScheduledDao;
import com.example.openbank.mapper.OpenBankTimingMapper;
import com.example.openbank.service.OpenBankTimingService;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.OpenBankSignParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OpenBankTimingServiceImp implements OpenBankTimingService {
    @Autowired
    private OpenBankTimingMapper openBnakTimingMapper;

    @Override
    public List<OpenBankScheduledDao> queryAll()throws EntpayException {
        try {
            return openBnakTimingMapper.queryAll();
        } catch (Exception e) {
            log.error("查询定时任务列表失败: {}",e.getMessage());
            throw new EntpayException("查询定时任务列表失败",e);
        }
    }

    @Override
    public OpenBankScheduledDao queryByOutId(String out_application_id)throws EntpayException {
        try {
            return openBnakTimingMapper.queryByOutId(out_application_id);
        } catch (Exception e) {
            log.error("签约定时任务:根据申请签约业务申请单号查询指定定时任务异常: {}",e.getMessage());
            throw new EntpayException("签约定时任务:根据申请签约业务申请单号查询指定定时任务异常",e);
        }
    }

    @Override
    public void insertOpenBankScheduledDao(OpenBankScheduledDao openBankScheduledDao) throws EntpayException {
        try {
            openBnakTimingMapper.insertOpenBankScheduledDao(openBankScheduledDao);
        } catch (Exception e) {
            log.error("申请签约定时任务：添加定时查询数据失败: {}",e.getMessage());
            throw new EntpayException("签约申请定时任务：添加定时查询数据失败",e);
        }
    }

    @Override
    public void deleteOpenBankScheduledDao(String out_application_id) throws EntpayException {
        try {
            openBnakTimingMapper.deleteOpenBankScheduledDao(out_application_id);
        } catch (Exception e) {
            log.error("申请签约定时任务：删除定时任务数据失败: {}",e.getMessage());
            throw new EntpayException("申请签约定时任务：删除定时任务数据失败",e);
        }
    }

    /**
     * 申请签约添加定时查询申请单重试策略
     * @param ent_id
     * @param openBank
     * @throws EntpayException
     */
    public void retryInsert(String ent_id, OpenBankSignParam openBank)throws EntpayException{
        int len = 3;
        for (int i = 1; i <= len; i++) {
            try {
            OpenBankScheduledDao openBankScheduledDao = queryByOutId(openBank.getOutApplicationId());
            if (openBankScheduledDao!=null){
                log.info("申请签约：添加定时任务申请单ent_id为"+ent_id+"的申请单,尝试重新添加定时执行单：成功");
                continue;
            }
                insertOpenBankScheduledDao(new OpenBankScheduledDao(ent_id,openBank.getOutApplicationId()));
                i--;
            } catch (EntpayException entpayException) {
                log.error("申请签约：添加定时任务申请单ent_id为"+ent_id+"的申请单异常,尝试重新添加定时执行单,第"+i+"次重试: 失败: {}",entpayException.getMessage());
                if (i == len){
                    log.error("申请签约：添加定时任务申请单ent_id为"+ent_id+"的申请单异常,重新添加定时执行单失败: {}",entpayException);
                    throw new EntpayException("申请签约：添加定时任务申请单ent_id为"+ent_id+"异常"+entpayException.getMessage()+",重新添加定时执行单失败",entpayException);
                }
            }
        }
    }
}
