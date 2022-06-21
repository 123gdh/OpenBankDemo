package com.example.openbank.service.serviceImp;

import com.example.openbank.controller.GoProduct;
import com.example.openbank.controller.OpenBank;
import com.example.openbank.dao.OpenBankDao;
import com.example.openbank.dao.OpenBankScheduledDao;
import com.example.openbank.dao.ProductOpenDao;
import com.example.openbank.enums.Errordescription;
import com.example.openbank.enums.Msg;
import com.example.openbank.enums.ResponseStatus;
import com.example.openbank.mapper.OpenBankMapper;
import com.example.openbank.result.Result;
import com.example.openbank.service.OpenBankService;
import com.example.openbank.service.OpenBankTimingService;
import com.example.openbank.utils.OptionUtils;
import com.example.openbank.utils.ResultUtils;
import com.example.openbank.vo.ApplicationStatusVerificationVo;
import com.tenpay.business.entpay.sdk.api.OpenBankSign;
import com.tenpay.business.entpay.sdk.api.ProductApplication;
import com.tenpay.business.entpay.sdk.api.Redirect;
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
import java.sql.Date;
import java.util.UUID;

@Service
@Slf4j
public class OpenBankServiceImp implements OpenBankService {
    @Autowired
    private OpenBankMapper openBankMapper;

    @Autowired
    private OpenBankTimingService openBankTimingService;

    @Autowired
    private OpenBankTimingServiceImp openBankTimingServiceImp;

    @Override
    public void updateState(String state, String out_application_id)throws EntpayException {
        try {
            openBankMapper.updateSignState(state,out_application_id);
        } catch (Exception e) {
            log.error("修改申请签约状态异常: {}",e.getMessage());
            throw new EntpayException("修改申请签约状态异常",e);
        }
    }

    @Override
    public void updateStateAndApplicationId(String state, String application_id, String out_application_id) throws EntpayException {
        try {
            openBankMapper.updateStateAndApplicationId(state,application_id,out_application_id);
        } catch (Exception e) {
            log.error("申请签约：添加银企付申请单号及修改申请签约状态为PROCESSING异常: {}",e.getMessage());
            throw new EntpayException("申请签约：添加银企付申请单号及修改申请签约状态为PROCESSING异常",e);
        }
    }

    @Override
    public void updateEntAcctId(String state, String ent_acct_id, String out_application_id) {
        openBankMapper.updateEntAcctId(state,ent_acct_id,out_application_id);
    }

    @Override
    @Transactional(rollbackFor = EntpayException.class)
    public ApplicationStatusVerificationVo stateVerification(OpenBankSignParam openBank)throws EntpayException{
        try {
            OpenBankDao productOpenDao = queryOpenBank(openBank.getBankAccount().getBankAccountName(),openBank.getBankAccount().getBankAccountNumber(),openBank.getBankAccount().getBankAbbreviation());
            Result result = null;
            String out_application_id = null;
            if (productOpenDao != null) {
                String desc = productOpenDao.getStatus();
                if (desc.equals(OpenBankSign.StatusEnum.PROCESSING.getDesc())) {
                    RedirectParam redirectParam = RedirectParam.builder()
                            .id(productOpenDao.getApplication_id()) // application_id
                            .type(RedirectTypeEnum.BANK_ACCOUNT_SIGN)
                            .build();
                    result =  OpenBank.signingJump(redirectParam);
                }
                if (desc.equals(OpenBankSign.StatusEnum.SUCCEEDED.getDesc())) {
                    String adopt = "已签约成功";
                    result = ResultUtils.success(ResponseStatus.SUCCESS.getdesc(), Msg.SUCCESS, adopt);
                }
                if (desc.equals(OpenBankSign.StatusEnum.FAILED.getDesc()) || desc.equals(OpenBankSign.StatusEnum.EXPIRED.getDesc()) || desc.equals(OpenBankSign.StatusEnum.REJECTED.getDesc())){
                    out_application_id = UUID.randomUUID().toString();
                    OpenBankDao openBankDao = new OpenBankDao();
                    openBankDao.setOut_application_id(out_application_id);
                    openBankDao.setBank_account_name(openBank.getBankAccount().getBankAccountName());
                    openBankDao.setBank_account_number(openBank.getBankAccount().getBankAccountNumber());
                    openBankDao.setBank_abbreviation(openBank.getBankAccount().getBankAbbreviation());
                    openBankDao.setStatus("开通中");
                    openBankDao.setCreate_date(new Date(System.currentTimeMillis()));
                    insertOpenBnak(productOpenDao);
                }
                if(desc.equals("开通中")){
                    out_application_id = productOpenDao.getOut_application_id();
                }
            }else {
                out_application_id = UUID.randomUUID().toString();
                OpenBankDao openBankDao = new OpenBankDao();
                openBankDao.setOut_application_id(out_application_id);
                openBankDao.setBank_account_name(openBank.getBankAccount().getBankAccountName());
                openBankDao.setBank_account_number(openBank.getBankAccount().getBankAccountNumber());
                openBankDao.setBank_abbreviation(openBank.getBankAccount().getBankAbbreviation());
                openBankDao.setStatus("开通中");
                openBankDao.setCreate_date(new Date(System.currentTimeMillis()));
                insertOpenBnak(openBankDao);
            }
            return new ApplicationStatusVerificationVo(result,out_application_id);
        } catch (EntpayException e) {
            throw new EntpayException(e);
        }
    }

    public void insertOpenBnak(OpenBankDao productOpenDao) throws EntpayException {
        try {
            openBankMapper.insertOpenBnak(productOpenDao);
        } catch (Exception e) {
            log.error("添加申请签约记录异常: {}",e.getMessage());
            throw new EntpayException("添加申请签约记录异常",e);
        }
    }

    @Override
    public OpenBankDao queryOpenBank(String bank_account_name, String bank_account_number, String bank_abbreviation)throws EntpayException {
        try {
            return openBankMapper.queryOpenBank(bank_account_name, bank_account_number, bank_abbreviation);
        } catch (Exception e) {
            log.error("查询签约申请异常: {}",e.getMessage());
            throw new EntpayException("查询签约申请异常",e);
        }
    }

    @Override
    public OpenBankDao queryOpenBankByOutApplicationId(String out_application_id) throws EntpayException {
        try {
            return openBankMapper.queryOpenBankByOutApplicationId(out_application_id);
        } catch (Exception e) {
            log.info("通过申请签约业务申请单号查询签约申请单异常: {}",e.getMessage());
            throw new EntpayException("通过申请签约业务申请单号查询签约申请单异常",e);
        }
    }

    /**
     * 发起签约
     * @param openBank
     * @param ent_id
     * @return
     * @throws EntpayException
     */
    @Transactional(rollbackFor = EntpayException.class)
    public OpenBankSign createOpenBank(OpenBankSignParam openBank,String ent_id) throws EntpayException{
        //存储需定时查询数据单号
        try {
            openBankTimingService.insertOpenBankScheduledDao(new OpenBankScheduledDao(ent_id,openBank.getOutApplicationId()));
        } catch (EntpayException e) {
            //异常重试策略
            try {
                openBankTimingServiceImp.retryInsert(ent_id,openBank);
            } catch (EntpayException entpayException) {
                log.error("申请签约：将签约申请数据存储到定时查询表异常: {}",e.getMessage());
                throw new EntpayException("申请签约：将签约申请数据存储到定时查询表异常",e);
            }
        }
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取本地IP异常: {}",e.getMessage(),e);
            throw new EntpayException("获取本地IP异常",e);
        }
        OpenBankSign byUI;
        try {
            OpenBankAccountRequest openBankAccountRequest = openBank.getBankAccount();
            NotifyUrl notifyUrl1 = openBank.getNotifyUrl();
            RequestOptions requestOptions = OptionUtils.getOp(ent_id);
            log.info("回调通知url");
            NotifyUrl notifyUrl = NotifyUrl.builder()
                    .serverNotifyUrl("http://10.43.26.46:8080/Callback/open-bank/sign")
                    .webSuccessUrl("http://"+hostAddress+":8080/openProducts/getOpenBankSuccess")
                    .webRefreshUrl("http://"+hostAddress+":8080/openProducts/getOpenBankError")
                    .build();

            OpenBankSignParam openBankSignParam = OpenBankSignParam.builder()
                    .outApplicationId(openBank.getOutApplicationId())
                    .bankAccount(openBankAccountRequest)
                    .notifyUrl(notifyUrl)
                    .build();
            byUI = OpenBankSign.createByUI(openBankSignParam, requestOptions);
        } catch (ApiException e) {
            log.error("申请签约：调用银企付接口申请签约异常: {}",e.getMessage());
            throw e;
        }
        //存储需定时动态修改状态申请单
        return byUI;
    }

    /**
     * 申请签约修改状态重试策略
     * @param openBankSign
     * @param openBank
     * @throws EntpayException
     */
    public void retryUpdete(OpenBankSign openBankSign,OpenBankSignParam openBank)throws EntpayException{
        int len = 3;
        for (int i = 1; i <= len; i++) {
            try {
                OpenBankDao productOpenDao = queryOpenBank(openBank.getBankAccount().getBankAccountName(),openBank.getBankAccount().getBankAccountNumber(),openBank.getBankAccount().getBankAbbreviation());
                if (!productOpenDao.getStatus().equals("开通中")){
                    log.error("申请签约：申请单号为"+openBank.getOutApplicationId()+"的产品修改申请签约状态为PROCESSING,尝试重新修改状态：成功");
                    continue;
                }
                updateStateAndApplicationId(OpenBankSign.StatusEnum.PROCESSING.getDesc(), openBankSign.getApplicationId(), openBank.getOutApplicationId());
                i--;
            } catch (EntpayException e) {
                log.error("申请签约：业务申请单号为"+openBank.getOutApplicationId()+"的产品修改申请签约状态为PROCESSING发生异常，尝试第"+i+"次重新修改状态: 失败: {}",e.getMessage());
                if (i == len){
                    log.error("申请签约：申请单号为"+openBank.getOutApplicationId()+"的产品修改申请签约状态为PROCESSING发生异常，重试修改状态失败: {}",e.getMessage());
                    throw new EntpayException("申请签约：申请单号为"+openBank.getOutApplicationId()+"的产品修改申请签约状态为PROCESSING发生异常，重试修改状态失败。"+e.getMessage(),e);
                }
            }
        }
    }

}
