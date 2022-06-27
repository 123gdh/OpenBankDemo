package com.example.openbank.timing;


import com.example.openbank.service.*;
import com.tenpay.business.entpay.sdk.api.OpenBankSupportBank;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@EnableScheduling
public class ScheduledTask {
    public static ConcurrentHashMap<String,OpenBankSupportBank> map = new ConcurrentHashMap();

    @Autowired
    private GoProductService goProductService;

    @Autowired
    private OpenBankTimingService openBankTimingService;

    @Autowired
    private ProductTimingService productTimingService;

    @Autowired
    private OpenBankTransferTimingService openBankTransferTimingService;

    @Autowired
    private OpenBankBatchTransferTimingService openBankBatchTransferTimingService;
//    /**
//     * 定时查询银行列表
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0/2 * * * ?")
//    public void getBankL() throws EntpayException {
//        try {
//            map.clear();
//            OpenBankSupportBankList response = OpenBankSupportBank.retrieve();
//            List<OpenBankSupportBank> bankList = response.getBanks();
//            for (OpenBankSupportBank bank : bankList) {
//                String bankName = bank.getBankName();
//                map.put(bankName, bank);
//            }
//            log.info(new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss+08:00").format(new Date())+"更新了支持银行列表信息");
//        } catch (EntpayException e) {
//            log.error("定时任务查询银行列表异常: {}",e.getMessage());
//        }
//    }
//
//    /**
//     * 定时校验十天未处理完成的申请单
//     * @throws Exception
//     */
//    @Scheduled(cron = "0 0/1 * * * ?")
//    public void overdueVerification() throws EntpayException {
//        log.info("定时校验十天未处理完成的申请单"+new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss+08:00").format(new Date()));
//        Calendar today = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"));
//        today.setTimeInMillis(new Date().getTime());
//        today.add(Calendar.DAY_OF_MONTH, -10);
//        java.sql.Date date = new java.sql.Date(today.getTimeInMillis());
//        List<ProductOpenDao> productOpenDaos = goProductService.queryOverdueApplication(date);
//        if (productOpenDaos != null && productOpenDaos.size()>0){
//            for (ProductOpenDao productOpenDao : productOpenDaos) {
//                goProductService.updateStateByUnifiedPname("FAILED",productOpenDao.getUnified_social_credit_code(),productOpenDao.getProduct_name());
//            }
//        }
//    }
//
//    /**
//     * 申请产品开通：定时更新申请产品开通状态
//     */
//    @Scheduled(cron = "0 0/2 * * * ?")
//    public void updateProductStatus() throws EntpayException {
//        log.info("定时任务：更新产品开通状态-"+new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss+08:00").format(new Date()));
//        List<ProductTimingDao> productTimingDaos = productTimingService.queryAll();
//        if (productTimingDaos.size() == 0){
//            return;
//        }
//        for (ProductTimingDao productTimingDao : productTimingDaos) {
//            try {
//                new QueryDetailsUtils().queryProductStatus(productTimingDao.getOut_request_no());
//            } catch (EntpayException e) {
//                log.error("申请产品开通：定时任务异常: {}",e.getMessage());
//            }
//        }
//    }
//
//    /**
//     * 申请签约：定时更新申请签约状态
//     */
//    @Scheduled(cron = "0 0/2 * * * ?")
//    public void updateOpenBankStatus() throws EntpayException {
//        log.info("定时任务：更新签约状态-"+new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss+08:00").format(new Date()));
//        List<OpenBankScheduledDao> openBankScheduledDaos = openBankTimingService.queryAll();
//        if (openBankScheduledDaos.size() == 0){
//            return;
//        }
//        for (OpenBankScheduledDao openBankScheduledDao : openBankScheduledDaos) {
//            try {
//            RequestOptions requestOptions = OptionUtils.getOp(openBankScheduledDao.getEnt_id());
//            new QueryDetailsUtils().querySign(openBankScheduledDao.getOut_application_id(),requestOptions);
//            } catch (EntpayException e) {
//                log.error("申请签约定时任务异常: {}",e.getMessage());
//            }
//        }
//    }
//
//    /**
//     * 单笔转账：定时更新单笔转账状态
//     * @throws EntpayException
//     */
//    @Scheduled(cron = "0 0/2 * * * ?")
//    public void updateOpenBankTransferStatus() throws EntpayException {
//        log.info("定时任务：更新单笔转账状态-"+new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss+08:00").format(new Date()));
//        List<OpenBankTransferTimingDao> openBankTransferTimingDaos = openBankTransferTimingService.queryAll();
//        if (openBankTransferTimingDaos.size() == 0){
//            return;
//        }
//        for (OpenBankTransferTimingDao openBankTransferTimingDao : openBankTransferTimingDaos) {
//            try {
//                RequestOptions requestOptions = OptionUtils.getOp(openBankTransferTimingDao.getEnt_id());
//                new QueryDetailsUtils().queryTransfers(openBankTransferTimingDao.getOut_transfer_id(),requestOptions);
//            } catch (EntpayException e) {
//                log.error("单笔转账定时任务异常: {}",e.getMessage());
//            }
//        }
//    }

    /**
     * 批量转账：定时更新批量转账状态
     * @throws EntpayException
     */
//    @Scheduled(cron = "0 0/2 * * * ?")
//    public void updateOpenBankBatchTransferStatus() throws EntpayException {
//        log.info("定时任务：更新批量转账状态-"+new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss+08:00").format(new Date()));
//        List<OpenBankBatchTransferTimingDao> openBankBatchTransferTimingDaos = openBankBatchTransferTimingService.queryAll();
//        if (openBankBatchTransferTimingDaos.size() == 0){
//            return;
//        }
//        for (OpenBankBatchTransferTimingDao openBankBatchTransferTimingDao : openBankBatchTransferTimingDaos) {
//            try {
//                RequestOptions requestOptions = OptionUtils.getOp(openBankBatchTransferTimingDao.getEnt_id());
//                QueryDetailsUtils.queryBatchTransfers(openBankBatchTransferTimingDao.getOut_batch_transfer_id(),requestOptions);
//            } catch (EntpayException e) {
//                log.error("批量转账定时任务异常: {}",e.getMessage());
//            }
//        }
//    }

}
