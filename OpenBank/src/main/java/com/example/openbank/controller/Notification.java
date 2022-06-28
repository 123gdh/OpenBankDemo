package com.example.openbank.controller;

import com.example.openbank.dao.OpenBankBatchTransferDao;
import com.example.openbank.dao.PayerDao;
import com.example.openbank.dao.ProductOpenDao;
import com.example.openbank.enums.Msg;
import com.example.openbank.enums.ResponseStatus;
import com.example.openbank.result.Result;
import com.example.openbank.result.WebhookResult;
import com.example.openbank.service.*;
import com.example.openbank.utils.OptionUtils;
import com.example.openbank.utils.QueryDetailsUtils;
import com.example.openbank.utils.ResultUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.tenpay.business.entpay.sdk.api.*;
import com.tenpay.business.entpay.sdk.common.NotifyHandler;
import com.tenpay.business.entpay.sdk.config.EntpayConfig;
import com.tenpay.business.entpay.sdk.exception.ApiException;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.*;
import com.tenpay.business.entpay.sdk.net.RequestOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/Callback")
@Slf4j
public class Notification {
    @Autowired
    private GoProductService goProductService;

    @Autowired
    private ProductTimingService productTimingService;

    @Autowired
    private PayerService payerService;

    @Autowired
    private OpenBankBatchTransferService openBankBatchTransferService;

    @Autowired
    private TransfersService transfersService;

    /**
     * 产品开通回调通知
     * @param request
     * @param body
     * @param auth
     * @return
     * @throws EntpayException
     */
    @Transactional(rollbackFor = EntpayException.class)
    @PostMapping("/product-application")
    public WebhookResult productApplicationOpenNotify(HttpServletRequest request,@RequestBody String body,@RequestHeader("TBEP-Authorization") String auth) throws EntpayException{
        try {
           log.info("{申请开通回调: {}}",request.getRequestURI());
           log.info("{申请开通回调body: {}}",body);
           ProductApplicationNotifyModel model = NotifyHandler.handlerWebhook(body, auth, ProductApplicationNotifyModel.class, EntpayConfig.getRealTbepPublicKey(null));
           log.info("mode={申请开通回调mode} {}",model);
            ProductOpenDao productOpenDao = goProductService.queryByOutRequestNo(model.getEventContent().getOutRequestNo());
            String sta = productOpenDao.getStatus();
            ProductApplication application = ProductApplication.retrieveByOutRequestNo(model.getEventContent().getOutRequestNo());
            String status = application.getProductDetails().get(0).getStatus().getDesc();
            if(sta.equals(AccountDetail.StatusEnum.INIT.getDesc())||sta.equals(AccountDetail.StatusEnum.PROCESSING.getDesc())){
                if (sta.equals(AccountDetail.StatusEnum.INIT.getDesc()) && status.equals(AccountDetail.StatusEnum.PROCESSING.getDesc())){
                    goProductService.updateStateByOutRequestNo(AccountDetail.StatusEnum.PROCESSING.getDesc(),model.getEventContent().getOutRequestNo());
                }
               if (status.equals(AccountDetail.StatusEnum.SUCCESS.getDesc())){
                   goProductService.updateStateByOutRequestNo(AccountDetail.StatusEnum.SUCCESS.getDesc(),model.getEventContent().getOutRequestNo());
                   OptionUtils.getOp(model.getEventContent().getEntId());
                   productTimingService.deleteProductTiming(application.getOutRequestNo());
               }else if (status.equals(AccountDetail.StatusEnum.FAILED.getDesc())){
                   goProductService.updateStateByOutRequestNo(AccountDetail.StatusEnum.FAILED.getDesc(),model.getEventContent().getOutRequestNo());
                   productTimingService.deleteProductTiming(application.getOutRequestNo());
               }
           }
           //处理成功，响应200
            return WebhookResult.success();
        } catch (EntpayException e) {
           log.error("产品开通回调接口异常: {}",e.getMessage());
           throw new EntpayException("产品开通回调接口异常",e);
       }
   }

    /**
     * 签约回调
     * @param request
     * @param body
     * @param auth
     * @return
     * @throws ApiException
     */
    @PostMapping("/open-bank/sign")
    public WebhookResult openBankSignNotify(HttpServletRequest request,@RequestBody String body,@RequestHeader("TBEP-Authorization") String auth) throws EntpayException {
        try {
            log.info("调用了签约回调");
            log.info(request.getRequestURI(),auth);
            log.info("{签约回调body} {}",body);
            OpenBankSignNotifyModel model = NotifyHandler.handlerWebhook(body, auth, OpenBankSignNotifyModel.class, EntpayConfig.getRealTbepPublicKey(null));
            RequestOptions requestOptions = OptionUtils.getOp(model.getEventContent().getEntId());
            new QueryDetailsUtils().querySign(model.getEventContent().getOutApplicationId(),requestOptions);
            return WebhookResult.success();
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new EntpayException("签约回调接口异常"+e);
        }
    }

    /**
     * 单笔转账回调
     * @param request
     * @param body
     * @param auth
     * @return
     * @throws EntpayException
     */
    @ResponseBody
    @PostMapping("/transfers")
    public WebhookResult transfersNotify(HttpServletRequest request, @RequestBody String body,
                                     @RequestHeader("TBEP-Authorization") String auth) throws EntpayException {
        try {
            log.info("{} {}", request.getRequestURI(), auth);
            log.info("{}", body);
            OpenBankTransferNotifyModel model = NotifyHandler.handlerWebhook(body, auth,
                    OpenBankTransferNotifyModel.class,
                    EntpayConfig.getRealTbepPublicKey(null));
            String outTransferId = model.getEventContent().getOutTransferId();
            PayerDao payerDao = payerService.queryEntIdByOutTransferId(model.getEventContent().getOutTransferId());
            RequestOptions requestOptions = OptionUtils.getOp(payerDao.getEnt_id());
            new QueryDetailsUtils().queryTransfers(outTransferId,requestOptions);
            return WebhookResult.success();
        } catch (EntpayException e) {
            log.info("单笔转账回调异常: {}",e.getMessage());
            throw new EntpayException("单笔转账回调异常",e);
        }
    }

    /**
     * 批量转账回调
     * @param request
     * @param body
     * @param auth
     * @return
     * @throws ApiException
     */
    @ResponseBody
    @PostMapping("/batch-transfers")
    public WebhookResult batchTransfersNotify(HttpServletRequest request, @RequestBody String body,
                                          @RequestHeader("TBEP-Authorization") String auth) throws EntpayException {
        try {
            log.info("{} {}", request.getRequestURI(), auth);
            log.info("{}", body);
            OpenBankBatchTransferNotifyModel model = NotifyHandler.handlerWebhook(body, auth,
                    OpenBankBatchTransferNotifyModel.class,
                    EntpayConfig.getRealTbepPublicKey(null));
            log.info("model={}", model);
            //查询数据库
            OpenBankBatchTransferDao openBankBatchTransferDao = openBankBatchTransferService.queryOpenBankBatchTransferDao(model.getEventContent().getOutBatchTransferId());
            String state = openBankBatchTransferDao.getBatch_transfer_status();
            if (!state.equals(OpenBankBatchTransfer.BatchTransferStatusEnum.FAILED.getDesc())){
                String eventType = model.getEventType();
                if (eventType.equals("batch_transfer.detail_revoked")){
                    List<String> revokedTransferDetailId = model.getEventContent().getRevokedTransferDetailId();
                    for (String out_transfer_detail_id : revokedTransferDetailId) {
                        transfersService.updateTransfersStatus(OpenBankBatchTransferDetail.TransferStatusEnum.REVOKED.getDesc(),out_transfer_detail_id);
                    }
                }else {
                    PayerDao payerDao = payerService.queryEntIdByOutBatchTransferId(model.getEventContent().getOutBatchTransferId());
                    RequestOptions requestOptions = OptionUtils.getOp(payerDao.getEnt_id());
                    new QueryDetailsUtils().queryBatchTransfers(openBankBatchTransferDao.getOut_batch_transfer_id(),requestOptions);
                }
            }
            return WebhookResult.success();
        } catch (EntpayException e) {
            log.info("批量转账回调接口异常: {}",e.getMessage());
            throw new EntpayException("批量转账回调接口异常:",e);
        }
    }


}
