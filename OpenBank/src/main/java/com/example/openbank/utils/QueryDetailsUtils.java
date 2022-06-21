package com.example.openbank.utils;

import com.example.openbank.controller.GoProduct;
import com.example.openbank.dao.*;
import com.example.openbank.service.*;
import com.example.openbank.service.serviceImp.*;
import com.tenpay.business.entpay.sdk.api.*;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.AccountDetail;
import com.tenpay.business.entpay.sdk.model.Certificate;
import com.tenpay.business.entpay.sdk.model.OpenBankBatchTransferDetail;
import com.tenpay.business.entpay.sdk.model.OpenBankTransferBankApprovalGuide;
import com.tenpay.business.entpay.sdk.net.RequestOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
public class QueryDetailsUtils{



    @RequestMapping("/queryDetailsUtilsTest")
    public void getDL(){
        System.out.println("a");
    }

    /**
     * 申请产品开通状态变更
     * @param out_request_no
     * @throws EntpayException
     */
    @Transactional(rollbackFor = EntpayException.class)
    public void queryProductStatus(String out_request_no)throws EntpayException{
        GoProductServiceImp goProductServiceImp = (GoProductServiceImp)ApplicationContextUtil.getBean("goProductServiceImp");
        ProductTimingService productTimingServiceImp = (ProductTimingServiceImp)ApplicationContextUtil.getBean("productTimingServiceImp");
        ProductOpenDao productOpenDao = goProductServiceImp.queryByOutRequestNo(out_request_no);
        if (productOpenDao == null){
            return;
        }
        String sta = productOpenDao.getStatus();
        try {
            ProductApplication application = ProductApplication.retrieveByOutRequestNo(out_request_no);
            String status = application.getProductDetails().get(0).getStatus().getDesc();
            if (sta.equals(AccountDetail.StatusEnum.INIT.getDesc()) && status.equals(AccountDetail.StatusEnum.PROCESSING.getDesc())){
                goProductServiceImp.updateStateByOutRequestNo(status,out_request_no);
            }
            if (!sta.equals(AccountDetail.StatusEnum.SUCCESS.getDesc()) && !sta.equals(AccountDetail.StatusEnum.FAILED.getDesc())){
                if (status.equals(AccountDetail.StatusEnum.SUCCESS.getDesc())){
                    if (!GoProduct.requestOptionsMap.containsKey(application.getEntId())){
                        goProductServiceImp.updateStateByOutRequestNo(AccountDetail.StatusEnum.SUCCESS.getDesc(),out_request_no);
                        Certificate certificate = Enterprise.retrieveCertificate(application.getEntId());
                        RequestOptions options = RequestOptions.getInstance()
                                .initOpenBank(certificate.getEntId(), certificate.getSerialNumber(), certificate.getEncryptedPrivateKey());
                        GoProduct.requestOptionsMap.put(application.getEntId(),options);
                    }
                    productTimingServiceImp.deleteProductTiming(out_request_no);
                }else if (status.equals(AccountDetail.StatusEnum.FAILED.getDesc())){
                    goProductServiceImp.updateStateByOutRequestNo(AccountDetail.StatusEnum.FAILED.getDesc(),out_request_no);
                    productTimingServiceImp.deleteProductTiming(out_request_no);
                }
            }
        } catch (EntpayException e) {
            log.error("定时任务:申请产品开通修改状态异常"+e.getMessage(),e);
            throw new EntpayException("定时任务：申请产品开通修改状态异常"+e.getMessage(),e);
        }
    }

    /**
     * 签约状态变更
     * @param out_application_id
     * @param requestOptions
     * @throws EntpayException
     */
    @Transactional(rollbackFor = EntpayException.class)
    public void querySign(String out_application_id, RequestOptions requestOptions) throws EntpayException {
        OpenBankService openBankService = (OpenBankService)ApplicationContextUtil.getBean("openBankServiceImp");
        OpenBankTimingService openBankTimingService = (OpenBankTimingServiceImp)ApplicationContextUtil.getBean("openBankTimingServiceImp");
        try {
            OpenBankDao openBankDao = openBankService.queryOpenBankByOutApplicationId(out_application_id);
            if (openBankDao == null){
                return;
            }
            String sta = openBankDao.getStatus();
            if (sta.equals(OpenBankSign.StatusEnum.PROCESSING.getDesc()) || sta.equals(OpenBankSign.StatusEnum.BANK_ACCEPTED.getDesc()) || sta.equals("开通中")){
                OpenBankSign response = OpenBankSign.retrieveByOutApplicationId(out_application_id, requestOptions);
                String desc = response.getStatus().getDesc();
                if (!desc.equals(OpenBankSign.StatusEnum.PROCESSING.getDesc()) && !desc.equals(OpenBankSign.StatusEnum.BANK_ACCEPTED.getDesc()) && !desc.equals("开通中")){
                    openBankTimingService.deleteOpenBankScheduledDao(out_application_id);
                }
                if (sta.equals(OpenBankSign.StatusEnum.PROCESSING.getDesc()) && !desc.equals(OpenBankSign.StatusEnum.PROCESSING.getDesc())){
                    openBankService.updateState(desc,out_application_id);
                }else if (sta.equals(OpenBankSign.StatusEnum.BANK_ACCEPTED.getDesc()) && (!desc.equals(OpenBankSign.StatusEnum.PROCESSING.getDesc()) && !desc.equals(OpenBankSign.StatusEnum.BANK_ACCEPTED.getDesc()))){
                    openBankService.updateState(desc,out_application_id);
                }
            }
        } catch (EntpayException e) {
            log.info("申请签约：定时修改/回单通知修改申请签约状态异常: {}",e.getMessage());
            throw new EntpayException("申请签约：定时修改/回单通知修改申请签约状态异常",e);
        }
    }


    /**
     * 单笔转账状态变更
     * @param outTransferId
     * @param requestOptions
     * @return
     * @throws EntpayException
     */
    @Transactional(rollbackFor = EntpayException.class)
    public void queryTransfers(String outTransferId, RequestOptions requestOptions) throws EntpayException {
        boolean flag = false;
        OpenBankTransferService openBankTransferService = (OpenBankTransferService)ApplicationContextUtil.getBean("openBankTransferServiceImp");
        OpenBankTransferTimingService openBankTransferTimingService = (OpenBankTransferTimingService)ApplicationContextUtil.getBean("openBankTransferTimingServiceImp");
        OpenBankTransferDao openBankTransferDao = openBankTransferService.queryByOutId(outTransferId);
        String sta=openBankTransferDao.getTransfer_status();
        if (!sta.equals(OpenBankTransfer.TransferStatusEnum.FAILED.getDesc()) && !sta.equals(OpenBankTransfer.TransferStatusEnum.REVOKED.getDesc())){
            OpenBankTransfer response;
            try {
                response = OpenBankTransfer.retrieveByOutTransferId(outTransferId, requestOptions);
            } catch (EntpayException e) {
                log.error("单笔转账定时查询：调用查询单笔转账接口查询单笔转账详情异常: {}",e.getMessage());
                throw new EntpayException("单笔转账定时查询：调用查询单笔转账接口查询单笔转账详情异常",e);
            }
            String desc = response.getTransferStatus().getDesc();
            try {
                if (desc.equals(OpenBankTransfer.TransferStatusEnum.FAILED.getDesc()) || desc.equals(OpenBankTransfer.TransferStatusEnum.REVOKED.getDesc())){
                    openBankTransferTimingService.deleteByOutId(outTransferId);
                }
                if (sta.equals("初始化") && desc.equals(OpenBankTransfer.TransferStatusEnum.PROCESSING.getDesc())){
                    openBankTransferService.updateStatusByOutId(desc,outTransferId);
                }
                if (sta.equals(OpenBankTransfer.TransferStatusEnum.PROCESSING.getDesc()) && !desc.equals(OpenBankTransfer.TransferStatusEnum.PROCESSING.getDesc())){
                    openBankTransferService.updateStatusByOutId(desc,outTransferId);
                }else if(sta.equals(OpenBankTransfer.TransferStatusEnum.BANK_ACCEPTED.getDesc()) && !desc.equals(OpenBankTransfer.TransferStatusEnum.PROCESSING.getDesc()) && !desc.equals(OpenBankTransfer.TransferStatusEnum.BANK_ACCEPTED.getDesc())){
                    openBankTransferService.updateStatusByOutId(desc,outTransferId);
                }else if (sta.equals(OpenBankTransfer.TransferStatusEnum.SUCCEEDED.getDesc()) && desc.equals(OpenBankTransfer.TransferStatusEnum.REVOKED.getDesc())){
                    openBankTransferService.updateStatusByOutId(OpenBankTransfer.TransferStatusEnum.REVOKED.getDesc(),outTransferId);
                }
            } catch (EntpayException e) {
                log.error("单笔转账定时查询：修改单笔转账状态异常: {}",e.getMessage());
                throw new EntpayException("单笔转账定时查询：修改单笔转账状态异常",e);
            }
        }else {
            openBankTransferTimingService.deleteByOutId(outTransferId);
        }
    }

    /**
     * 批量转账状态变更
     * @param outBatchTransferId
     * @param requestOptions
     * @return
     * @throws EntpayException
     */
    @Transactional(rollbackFor = EntpayException.class)
    public void queryBatchTransfers(String outBatchTransferId,RequestOptions requestOptions) throws EntpayException {
        try {
            OpenBankBatchTransferServiceImp openBankBatchTransferService = (OpenBankBatchTransferServiceImp)ApplicationContextUtil.getBean("openBankBatchTransferServiceImp");
            OpenBankBatchTransferTimingService openBankBatchTransferTimingService = (OpenBankBatchTransferTimingService)ApplicationContextUtil.getBean("openBankBatchTransferTimingServiceImp");
            OpenBankBatchTransferDao openBankBatchTransferDao = openBankBatchTransferService.queryOpenBankBatchTransferDao(outBatchTransferId);
            String sta = openBankBatchTransferDao.getBatch_transfer_status();
            //判定本地批量转账状态是否为终态
            if (!sta.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.FAILED.getDesc()) && !sta.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.FINISHED.getDesc())){
                OpenBankBatchTransfer response = OpenBankBatchTransfer.retrieveByOutBatchTransferId(outBatchTransferId, requestOptions);
                String desc = response.getBatchTransferStatus().getDesc();
                List<OpenBankBatchTransferDetail> transfers = response.getTransfers();
                //判定本地批量转账为INIT状态及其处理方式
                if ((sta.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.INIT.getDesc())) && !desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.INIT.getDesc())){
                    if (desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.PROCESSING.getDesc())) {
                        openBankBatchTransferService.updateStatus(response.getTransfers(),response.getOutBatchTransferId(),OpenBankBatchTransfer.BatchTransferStatusEnum.PROCESSING.getDesc());
                    }else if (desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.BANK_ACCEPTED.getDesc())){
                        openBankBatchTransferService.updateStatus(response.getTransfers(),response.getOutBatchTransferId(),OpenBankBatchTransfer.BatchTransferStatusEnum.BANK_ACCEPTED.getDesc());
                    }else if (desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.FAILED.getDesc())){
                        openBankBatchTransferService.updateStatus(response.getTransfers(),response.getOutBatchTransferId(),OpenBankBatchTransfer.BatchTransferStatusEnum.FAILED.getDesc());
                        openBankBatchTransferTimingService.deleteOpenBankBatchTransferTiming(response.getOutBatchTransferId());
                    }else if (desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.FINISHED.getDesc())){
                        openBankBatchTransferService.updateStatusByOutId(OpenBankBatchTransfer.BatchTransferStatusEnum.FINISHED.getDesc(),response.getOutBatchTransferId());
                        openBankBatchTransferTimingService.deleteOpenBankBatchTransferTiming(response.getOutBatchTransferId());
                        statusByFailed(transfers);
                    }
                }
                //判定本地批量转账为PROCESSING状态及其处理方式
                if(sta.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.PROCESSING.getDesc()) && !desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.INIT.getDesc()) && !desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.PROCESSING.getDesc())){
                    //修改子细明状态，如果为批量总状态为BANK_ACCEPTED，则修改为BANK_ACCEPTED，如果总状态为SUCCEEDED，这修改子状态为SUCCESS或FFAILED.
                    if (desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.BANK_ACCEPTED.getDesc())){
                        openBankBatchTransferService.updateStatus(response.getTransfers(),response.getOutBatchTransferId(),OpenBankBatchTransfer.BatchTransferStatusEnum.BANK_ACCEPTED.getDesc());
                    }else if (desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.FINISHED.getDesc())){
                        openBankBatchTransferTimingService.deleteOpenBankBatchTransferTiming(response.getOutBatchTransferId());
                        openBankBatchTransferService.updateStatusByOutId(OpenBankBatchTransfer.BatchTransferStatusEnum.FINISHED.getDesc(),response.getOutBatchTransferId());
                        statusByFailed(transfers);
                    }else if (desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.FAILED.getDesc())){
                        openBankBatchTransferTimingService.deleteOpenBankBatchTransferTiming(response.getOutBatchTransferId());
                        openBankBatchTransferService.updateStatus(response.getTransfers(),response.getOutBatchTransferId(),OpenBankBatchTransfer.BatchTransferStatusEnum.FAILED.getDesc());
                    }
                }
                //判定本地批量转账为BANK_ACCEPTED状态及其处理方式
                if(sta.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.BANK_ACCEPTED.getDesc()) && !desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.INIT.getDesc()) && !desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.PROCESSING.getDesc()) && !desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.BANK_ACCEPTED.getDesc())){
                    if (desc.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.FINISHED.getDesc())){
                        openBankBatchTransferTimingService.deleteOpenBankBatchTransferTiming(response.getOutBatchTransferId());
                        openBankBatchTransferService.updateStatusByOutId(OpenBankBatchTransfer.BatchTransferStatusEnum.FINISHED.getDesc(),response.getOutBatchTransferId());
                        statusByFailed(transfers);;
                    }
                }
            }
        } catch (EntpayException e) {
            log.info("查询批量转账异常"+e.getMessage());
            throw new EntpayException("查询批量转账异常"+e.getMessage(),e);
        }
    }

    public static void statusByFailed(List<OpenBankBatchTransferDetail> transfers) throws EntpayException {
        TransfersServiceImp transfersServiceImp = (TransfersServiceImp)ApplicationContextUtil.getBean("transfersServiceImp");
        for (OpenBankBatchTransferDetail transfer : transfers) {
            String state = transfer.getTransferStatus().getDesc();
            if (state.equals(OpenBankBatchTransferDetail.TransferStatusEnum.FAILED.getDesc())){
                transfersServiceImp.updateTransfersStatus(OpenBankBatchTransferDetail.TransferStatusEnum.FAILED.getDesc(),transfer.getOutTransferDetailId());
            }else if (state.equals(OpenBankBatchTransferDetail.TransferStatusEnum.SUCCEEDED.getDesc())){
                transfersServiceImp.updateTransfersStatus(OpenBankBatchTransferDetail.TransferStatusEnum.SUCCEEDED.getDesc(), transfer.getOutTransferDetailId());
            }else if (state.equals(OpenBankBatchTransferDetail.TransferStatusEnum.REVOKED.getDesc())){
                transfersServiceImp.updateTransfersStatus(OpenBankBatchTransferDetail.TransferStatusEnum.REVOKED.getDesc(), transfer.getOutTransferDetailId());
            }
        }
    }
}
