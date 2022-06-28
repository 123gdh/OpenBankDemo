package com.example.openbank.controller;

import com.example.openbank.enums.Msg;
import com.example.openbank.enums.ResponseStatus;
import com.example.openbank.pojo.ObviousErrorList;
import com.example.openbank.result.Result;
import com.example.openbank.result.WebhookResult;
import com.example.openbank.service.OpenBankService;
import com.example.openbank.service.serviceImp.OpenBankBatchTransferServiceImp;
import com.example.openbank.service.serviceImp.OpenBankServiceImp;
import com.example.openbank.service.serviceImp.OpenBankTransferServiceImp;
import com.example.openbank.utils.OptionUtils;
import com.example.openbank.utils.ResultUtils;
import com.example.openbank.vo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tenpay.business.entpay.sdk.api.OpenBankFileDownload;
import com.tenpay.business.entpay.sdk.api.*;
import com.tenpay.business.entpay.sdk.common.FileDownloadResponse;
import com.tenpay.business.entpay.sdk.exception.ApiException;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.*;
import com.tenpay.business.entpay.sdk.net.RequestOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
@Slf4j
public class OpenBank {
    @Autowired
    private OpenBankService service;

    @Autowired
    private OpenBankServiceImp openBankServiceImp;

    @Autowired
    private OpenBankTransferServiceImp openBankTransferServiceImp;

    @Autowired
    private OpenBankBatchTransferServiceImp openBankBatchTransferServiceImp;


    /**
     * 签约申请
     * @param openBankSignParamEid
     * @return
     * @throws EntpayException
     */
    @PostMapping("/open-bank/sign/ui")
    public Result applyForSigning(@RequestBody OpenBankSignParamEid openBankSignParamEid) throws EntpayException{
        String ent_id = openBankSignParamEid.getEnt_id();
        OpenBankSignParam openBank = openBankSignParamEid.getOpenBankSignParam();
        ApplicationStatusVerificationVo applicationStatusVerificationVo;
        //申请签约状态校验
        try {
            applicationStatusVerificationVo = service.stateVerification(openBank);
        } catch (EntpayException e) {
            log.error("申请签约：状态校验异常: {}",e.getMessage());
            throw new EntpayException("申请签约：状态校验异常",e);
        }
        if (applicationStatusVerificationVo.getResult()!=null){
            return applicationStatusVerificationVo.getResult();
        }else if(StringUtils.hasText(applicationStatusVerificationVo.getOutRequestNo())){
            openBank.setOutApplicationId(applicationStatusVerificationVo.getOutRequestNo());
        }
        //调用申请接口
        OpenBankSign openBankSign;
        try {
            openBankSign = openBankServiceImp.createOpenBank(openBank,ent_id);
        } catch (ApiException e) {
            if (ObviousErrorList.openbankErroeList.contains(e.getError().getCode())) {
                service.updateState(OpenBankSign.StatusEnum.FAILED.getDesc(),openBank.getOutApplicationId());
                log.error("申请签约：明确申请签约异常，申请签约失败: {}",e.getMessage());
                throw new EntpayException("申请签约：明确申请签约异常，申请签约失败",e);
            }else {
                log.error("申请签约：未知申请签约异常，请重新申请签约: {}",e.getMessage());
                throw new EntpayException("申请签约：未知申请签约异常，请重新申请签约",e);
            }
        }
        //修改签约状态为处理中
        try {
            service.updateStateAndApplicationId(OpenBankSign.StatusEnum.PROCESSING.getDesc(),openBankSign.getApplicationId(),openBank.getOutApplicationId());
        } catch (EntpayException e) {
            log.error("更新申请签约状态异常：状态修改为PROCESSING异常: {}",e.getMessage());
            openBankServiceImp.retryUpdete(openBankSign,openBank);
        }
        RedirectParam redirectParam = RedirectParam.builder()
                .id(openBankSign.getApplicationId())
                .type(RedirectTypeEnum.BANK_ACCOUNT_SIGN)
                .build();
        Result result;
        //创建跳转链接
        try {
            result = signingJump(redirectParam);
        } catch (EntpayException e) {
            log.error("申请产品签约响应成功，但创建跳转链接失败，请重新调用创建跳转链接接口"+e.getMessage());
            throw new EntpayException("申请产品签约响应成功，但创建跳转链接失败，请重新调用创建跳转链接接口",e);
        }
        return result;
    }

    /**
    * 签约跳转：获取跳转地址
    * @throws EntpayException
    */
    @PostMapping("/redirects")
    public static Result signingJump(@RequestBody RedirectParam redirectParam) throws EntpayException {
        try {
            Redirect response = Redirect.create(redirectParam);
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,response);
        } catch (EntpayException e) {
            log.error("申请签约：创建签约跳转链接异常: {}",e.getMessage());
            throw new EntpayException("申请签约：创建签约跳转链接对象异常",e);
        }

    }

    /**
     * 查询签约信息：通过银企支付申请单号
     * @param application_id
     * @return
     * @throws EntpayException
     */
    @ResponseBody
    @GetMapping("/open-bank/sign/{application_id}")
    public Result productLaunchDetails(@PathVariable(value = "application_id") String application_id,@RequestBody String ent_id) throws EntpayException{
        try {
            RequestOptions requestOptions = OptionUtils.getOp(ent_id);
            OpenBankSign  balance;
            if (StringUtils.hasText(application_id)){
                //测试一律使用平台申请单号查询
                balance = OpenBankSign.retrieve(application_id,requestOptions);
            }else{
                throw new EntpayException("申请签约：银企支付申请单号为空");
            }
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,balance);
        } catch (EntpayException e) {
            log.error("申请签约：通过银企支付申请单号查询产品签约信息异常: {}",e.getMessage());
            throw new EntpayException("申请签约：通过银企支付申请单号查询产品签约信息异常",e);
        }
    }

    /**
     * 查询签约信息：通过平台业务申请单号
     * @param out_application_id
     * @param ent_id
     * @return
     * @throws EntpayException
     */
    @ResponseBody
    @GetMapping("/open-bank/sign/out-application-id/{out_application_id}")
    public Result productLaunchDetailsOutId(@PathVariable(value = "out_application_id") String out_application_id,@RequestBody String ent_id) throws EntpayException{
        try {
            RequestOptions requestOptions = OptionUtils.getOp(ent_id);
            OpenBankSign  balance;
            if (StringUtils.hasText(out_application_id)){
                //测试一律使用平台申请单号查询
                balance = OpenBankSign.retrieveByOutApplicationId(out_application_id,requestOptions);
            }else{
                throw new EntpayException("申请签约：平台业务申请编号为空");
            }
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,balance);
        } catch (EntpayException e) {
            log.error("申请签约：通过平台业务申请编号查询产品签约信息异常: {}",e.getMessage());
            throw new EntpayException("申请签约：通过平台业务申请编号查询产品签约信息异常",e);
        }
    }

    /**
     * 查询余额
     * @param ent_acct_id
     * @return
     * @throws EntpayException
     */
    @ResponseBody
    @GetMapping("/accounts/open-bank/{ent_acct_id}/balance")
    public Result CheckBalance(@PathVariable(value = "ent_acct_id") String ent_acct_id,@RequestBody String entId) throws EntpayException {
        try {
            RequestOptions requestOptions = OptionUtils.getOp(entId);
            OpenAccountBalance balance = OpenBankAccount.retrieveBalance(ent_acct_id, requestOptions);
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,balance);
        } catch (EntpayException e) {
            log.error("查询账户余额异常：ent_acct_id为："+ent_acct_id+"查询异常: {}",e.getMessage());
            throw new EntpayException("查询账户余额异常：ent_acct_id为："+ent_acct_id+"查询异常",e);
        }
    }

    /**
     * 单笔转账
     * @param openBankTransferParam
     * @return
     * @throws EntpayException
     */
    @ResponseBody
    @PostMapping("/transfers")
    public Result transferAccounts(@RequestBody OpenBankTransferParam openBankTransferParam) throws  EntpayException {
        try {
            String out_transfer_id = openBankTransferServiceImp.createAndInsertOpenBankTransfer(openBankTransferParam);
            openBankTransferParam.setOutTransferId(out_transfer_id);
        } catch (EntpayException e) {
            log.error("单笔转账：存储单笔转账详情异常:{}",e.getMessage());
            throw new EntpayException("单笔转账：存储单笔转账详情异常",e);
        }
        OpenBankTransfer transfer;
        try {
            transfer = openBankTransferServiceImp.createTransfer(openBankTransferParam);
        } catch (ApiException e) {
            openBankTransferServiceImp.updateStatusByOutId(OpenBankTransfer.TransferStatusEnum.FAILED.getDesc(),openBankTransferParam.getOutTransferId());
            log.error("申请产品开通：明确异常，申请产品开通失败:{}",e.getMessage());
            throw e;
        }
        try {
            openBankTransferServiceImp.updateStatusByOutId(OpenBankTransfer.TransferStatusEnum.PROCESSING.getDesc(),openBankTransferParam.getOutTransferId());
        } catch (EntpayException e) {
            openBankTransferServiceImp.retryUpdete(openBankTransferParam);
        }
        return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,transfer);
    }

    /**
     * 查询单笔转账：通过银企付转账单号查询
     * @param transfer_id
     * @throws EntpayException
     */
    @GetMapping("/transfers/{transfer_id}")
    public Result queryTransfer(@PathVariable String transfer_id,@RequestBody String ent_id) throws EntpayException {
        try {
            OpenBankTransfer response;
            RequestOptions requestOptions = OptionUtils.getOp(ent_id);
            if (!StringUtils.hasText(transfer_id)){
                throw new EntpayException("查询单笔转账：银企支付转账单号为空异常");
            }
            response = OpenBankTransfer.retrieve(transfer_id, requestOptions);
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,response);
        } catch (EntpayException e) {
            log.error("查询单笔转账：通过银企付转账单号查询单笔转账详情异常: {}",e.getMessage());
            throw new EntpayException("查询单笔转账：通过银企付转账单号查询单笔转账详情异常",e);
        }
    }

    /**
     * 查询单笔转账：通过平台转账单号查询
     * @param out_transfer_id
     * @throws EntpayException
     */
    @GetMapping("/transfers/out-transfer-id/{out_transfer_id}")
    public Result queryTransferByOutId(@PathVariable String out_transfer_id,@RequestBody String ent_id) throws EntpayException {
        try {
            OpenBankTransfer response;
            RequestOptions requestOptions = OptionUtils.getOp(ent_id);
            if (!StringUtils.hasText(out_transfer_id)){
                throw new EntpayException("查询单笔转账：银企支付转账单号为空异常");
            }
            response = OpenBankTransfer.retrieveByOutTransferId(out_transfer_id, requestOptions);
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,response);
        } catch (EntpayException e) {
            log.error("查询单笔转账：通过平台转账单号查询单笔转账详情异常: {}",e.getMessage());
            throw new EntpayException("查询单笔转账；通过平台转账单号查询单笔转账详情异常",e);
        }
    }

    /**
     * 批量转账
     * @param openBankBatchTransferParam
     */
    @PostMapping("/batch-transfers")
    public Result batchTransfer(@RequestBody OpenBankBatchTransferParam  openBankBatchTransferParam) throws EntpayException{
        if (openBankBatchTransferParam.getTotalNum()>500){
            throw new EntpayException("批量转账，一次转账数量不能超过500");
        }
        openBankBatchTransferParam = openBankBatchTransferServiceImp.createOpenBankBatchTransfer(openBankBatchTransferParam);
        OpenBankBatchTransfer openBankBatchTransfer;
        try {
            openBankBatchTransfer = openBankBatchTransferServiceImp.create(openBankBatchTransferParam);
        } catch (EntpayException e) {
            openBankBatchTransferServiceImp.updateStatusParam(openBankBatchTransferParam.getTransfers(),openBankBatchTransferParam.getOutBatchTransferId(),OpenBankBatchTransfer.BatchTransferStatusEnum.FAILED.getDesc());
            log.error("批量转账：调用银企付申请批量转账接口异常: {}",e.getMessage());
            throw new EntpayException("申请签约：调用银企付申请批量转账接口异常",e);
        }
        try {
            openBankBatchTransferServiceImp.updateBatchTransferId(openBankBatchTransfer.getBatchTransferId(),openBankBatchTransfer.getOutBatchTransferId());
            openBankBatchTransferServiceImp.updateStatusParam(openBankBatchTransferParam.getTransfers(),openBankBatchTransferParam.getOutBatchTransferId(),OpenBankBatchTransfer.BatchTransferStatusEnum.PROCESSING.getDesc());
        } catch (EntpayException e) {
            log.error("批量转账：修改批量转账状态为PROCESSING异常");
        }
        return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,openBankBatchTransfer);
    }

    //查询批量转账
    @GetMapping("/batch-transfers/out-batch-transfer-id/{out_batch_transfer_id}")
    public Result queryBatchTransfer(@PathVariable(value = "out_batch_transfer_id") String out_batch_transfer_id,@RequestBody String entId) throws EntpayException {
        try {
            if (!StringUtils.hasText(out_batch_transfer_id)){
                throw new EntpayException("平台批量转账批次号为空");
            }
            RequestOptions requestOptions = OptionUtils.getOp(entId);
            OpenBankBatchTransfer response = OpenBankBatchTransfer.retrieveByOutBatchTransferId(out_batch_transfer_id, requestOptions);
            return ResultUtils.success(ResponseStatus.SUCC.getdesc(),Msg.SUCCESS,response);
        } catch (EntpayException e) {
            log.info("查询批量转账异常"+e.getMessage());
            throw new EntpayException("查询批量转账异常"+e.getMessage(),e);
        }
    }

    //查询批量转账
    @GetMapping("/batch-transfers/{batch_transfer_id}")
    public Result queryBatchTransferByOutId(@PathVariable(value = "batch_transfer_id") String batch_transfer_id,@RequestBody String entId) throws EntpayException {
        try {
            if (!StringUtils.hasText(batch_transfer_id)){
                throw new EntpayException("银企支付批量批次号为空");
            }
            RequestOptions requestOptions = OptionUtils.getOp(entId);
            OpenBankBatchTransfer response = OpenBankBatchTransfer.retrieve(batch_transfer_id, requestOptions);
            return ResultUtils.success(ResponseStatus.SUCC.getdesc(),Msg.SUCCESS,response);
        } catch (EntpayException e) {
            log.info("查询批量转账异常"+e.getMessage());
            throw new EntpayException("查询批量转账异常"+e.getMessage(),e);
        }
    }

    /**
     * 根据银行账号匹配联行号
     * @param vo
     * @return
     * @throws EntpayException
     */
    @PostMapping("/bank-info/branches/bank-account-number")
    public Result getBankBranchId(@RequestBody BankVo vo) throws EntpayException {
        String bank_account_number = vo.getBank_account_number();
        String bank_name = vo.getBank_name();
        BankInfoAccountNumberParam bankInfoAccountNumberParam = BankInfoAccountNumberParam.builder()
                .bankName(bank_name)
                .bankAccountNumber(bank_account_number)
                .build();
        BankInfo response = BankInfo.retrieveBranchesByBankAccountNumber(bankInfoAccountNumberParam);
        return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,response);
    }


//    ----------  流水回单  ------------

    /**
     * 查询流水
     * @param ent_acct_id
     * @param entId
     * @return
     * @throws EntpayException
     */
    @GetMapping("/accounts/open-bank/{ent_acct_id}/statements")
    public Result  queryStatement(@PathVariable(value = "ent_acct_id") String ent_acct_id,@RequestBody String entId) throws EntpayException{
        RetrieveStatementGetParam retrieveStatementGetParam = new RetrieveStatementGetParam();
        retrieveStatementGetParam.setPageNo(1);
        retrieveStatementGetParam.setPageSize(10);
        retrieveStatementGetParam.setOrderBy(RetrieveStatementGetParam.OrderByEnum.DESCENDING);
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.YEAR,-1);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.YEAR,1);
        retrieveStatementGetParam.setBeginDate(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        retrieveStatementGetParam.setEndDate(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        RequestOptions requestOptions = OptionUtils.getOp(entId);
        OpenBankAccountStatement statement;
        try {
            statement = OpenBankAccount
                    .retrieveStatement(ent_acct_id,retrieveStatementGetParam, requestOptions);
        } catch (EntpayException e) {
            log.error("查询企业签约账户流水异常"+e.getMessage());
            throw new EntpayException("查询企业签约账户流水异常"+e.getMessage(),e);
        }
        return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,statement);
    }

    /**
     * 回单下载：通过申请回调token查询下载地址并下载账单文件
     * @param ent_acct_id
     * @param receiptsTokenVo
     * @param response
     * @throws EntpayException
     * @throws IOException
     */
    @PostMapping("/accounts/open-bank/{ent_acct_id}/receipts")
    public void receiptDownloadByToken (@PathVariable(value = "ent_acct_id") String ent_acct_id, @RequestBody ReceiptsTokenVo receiptsTokenVo, HttpServletResponse response) throws EntpayException, IOException {
        String receiptToken = receiptsTokenVo.getToken();
        String ent_id = receiptsTokenVo.getEnt_id();
        RequestOptions options = OptionUtils.getOp(ent_id);
        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        OpenBankReceiptDownload retrieveReceipt;
        FileDownloadResponse fileDownloadResponse;
        try {
            OpenBankReceiptDownloadParam openBankReceiptDownloadParam = OpenBankReceiptDownloadParam.builder()
                    .receiptToken(receiptToken)
                    .build();
            // 获取文件下载地址
            retrieveReceipt = OpenBankReceiptDownload.retrieveReceipt(ent_acct_id,
                    openBankReceiptDownloadParam, options);
            // 获取解密后的文件输入流
            fileDownloadResponse = retrieveReceipt.download(options);
        }catch (EntpayException e) {
            ApiException apierror = (ApiException) e;
            log.error("通过申请回调token查询下载地址并下载账单文件异常"+apierror.getError().getDesc()+e.getMessage());
            throw new EntpayException("通过申请回调token查询下载地址并下载账单文件异常"+apierror.getError().getDesc()+e.getMessage());
        }
        try {
            if (retrieveReceipt.getReceiptStatus().equals(OpenBankReceiptDownload.ReceiptStatusEnum.READY)) {
                response.setContentType("application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("Receipt.PDF", "UTF-8"));
                bufferedOutputStream = new BufferedOutputStream(response.getOutputStream(),2048);
                InputStream inputStream = fileDownloadResponse.getInputStream();
                bufferedInputStream = new BufferedInputStream(inputStream,2048);
                int len;
                while ((len = bufferedInputStream.read()) != -1){
                    bufferedOutputStream.write(len);
                }
            }else {
                throw new EntpayException("您的回单文件正在处理中");
            }
        } catch (IOException e) {
            log.error("根据回单申请token下载转账回单文件异常"+e.getMessage());
            throw new EntpayException("根据回单申请token下载转账回单文件异常"+e.getMessage(),e);
        } finally {
            if (bufferedInputStream != null){
                bufferedInputStream.close();
            }
            if (bufferedOutputStream != null){
                bufferedOutputStream.close();
            }
        }
    }

    /**
     * 回单下载：通过指定日期账单查询下载地址并下载回调文件
     * @param ent_acct_id
     * @param receiptsDateVo
     * @param response
     * @throws EntpayException
     * @throws IOException
     */
    @PostMapping("/accounts/open-bank/{ent_acct_id}/receipts/batch")
    public void receiptDownloadByDate (@PathVariable(value = "ent_acct_id") String ent_acct_id,@RequestBody ReceiptsDateVo receiptsDateVo, HttpServletResponse response) throws EntpayException, IOException {
        //根据日期获取多笔回单的下载地址
        String ent_id = receiptsDateVo.getEnt_id();
        Date date = receiptsDateVo.getQuery_date();
        String queryDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        RequestOptions options = OptionUtils.getOp(ent_id);
        //获取回单inputstream流
        OpenBankBatchReceiptDownloadParam openBankBatchReceiptDownloadParam = OpenBankBatchReceiptDownloadParam.builder()
                .queryDate(queryDate)
                .build();
        ZipInputStream zis = null;
        ZipOutputStream zio = null;
        OpenBankReceiptDownload retrieveReceipt;
        FileDownloadResponse fileDownloadResponse;
        try {
            retrieveReceipt = OpenBankReceiptDownload.retrieveBatchReceipts(ent_acct_id,
                    openBankBatchReceiptDownloadParam, options);
            fileDownloadResponse = retrieveReceipt.download(options);
        }catch (EntpayException e) {
            ApiException apierror = (ApiException) e;
            log.error("通过指定日期账单查询下载地址并下载回调文件"+apierror.getError().getDesc()+e.getMessage());
            throw new EntpayException("通过指定日期账单查询下载地址并下载回调文件"+apierror.getError().getDesc()+e.getMessage());
        }
        try{
            //响应输出流
            if (retrieveReceipt.getReceiptStatus().equals(OpenBankReceiptDownload.ReceiptStatusEnum.READY)) {
                response.setContentType("application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("Receipt.zip", "UTF-8"));
                InputStream inputStream = fileDownloadResponse.getInputStream();
                zis = new ZipInputStream(new BufferedInputStream(inputStream,2048));
                zio = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream(),2048));
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null){
                    int len;
                    zio.putNextEntry(new ZipEntry(entry.getName()));
                    while ((len = zis.read()) != -1){
                        zio.write(len);
                    }
                    zio.closeEntry();
                }
            }else {
                throw new EntpayException("您的回单文件正在处理中");
            }
        } catch (IOException e) {
            log.error("根据指定日期下载转账回单文件异常"+e.getMessage());
            throw new EntpayException("根据指定日期下载转账回单文件异常"+e.getMessage(),e);
        }finally {
            if (zis != null){
                zis.close();
            }
            if (zio!=null){
                zio.close();
            }
        }
    }

    /**
     * 凭证下载：根据银企支付单号查询并下载凭证文件
     * @param ent_acct_id
     * @param receiptsOrderIdVo
     * @param response
     * @throws EntpayException
     * @throws IOException
     */
    @PostMapping("/accounts/open-bank/{ent_acct_id}/vouchers")
    public void receiptDownloadByOrderId (@PathVariable(value = "ent_acct_id") String ent_acct_id,@RequestBody ReceiptsOrderIdVo receiptsOrderIdVo,HttpServletResponse response) throws EntpayException, IOException {
        //根据商企付单号获取回单下载地址
        String orderId = receiptsOrderIdVo.getOrder_id();
        String trade_type = receiptsOrderIdVo.getTrade_type();
        String ent_id = receiptsOrderIdVo.getEnt_id();
        VoucherTradeTypeEnum voucherTradeTypeEnum = VoucherTradeTypeEnum.valueOf(trade_type);
        RequestOptions options = OptionUtils.getOp(ent_id);
        ZipInputStream zis = null;
        ZipOutputStream zio = null;
        OpenBankVoucherDownloadParam openBankVoucherDownloadParam = OpenBankVoucherDownloadParam.builder()
                .orderId(orderId)
                .tradeType(voucherTradeTypeEnum)
                .build();
        OpenBankReceiptDownload retrieveReceipt;
        FileDownloadResponse fileDownloadResponse;
        try {
            retrieveReceipt = OpenBankReceiptDownload.retrieveVoucher(ent_acct_id, openBankVoucherDownloadParam, options);
            fileDownloadResponse =retrieveReceipt.download(options);
        } catch (EntpayException e) {
            ApiException apierror = (ApiException) e;
            log.error("根据银企支付单号查询并下载凭证文件异常"+apierror.getError().getDesc()+e.getMessage());
            throw new EntpayException("根据银企支付单号查询并下载凭证文件异常"+apierror.getError().getDesc()+e.getMessage());
        }
        try{
            //响应输出流
            if (retrieveReceipt.getReceiptStatus().equals(OpenBankReceiptDownload.ReceiptStatusEnum.READY)) {
                response.setContentType("application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("Receipt.zip", "UTF-8"));
                InputStream inputStream = fileDownloadResponse.getInputStream();
                zis = new ZipInputStream(new BufferedInputStream(inputStream,2048));
                zio = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream(),2048));
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null){
                    int len;
                    zio.putNextEntry(new ZipEntry(entry.getName()));
                    while ((len = zis.read()) != -1){
                        zio.write(len);
                    }
                    zio.closeEntry();
                }
            }else {
                throw new EntpayException("您的回单文件正在处理中");
            }
        } catch (IOException e) {
            log.error("根据银企支付单号下载转账回单文件异常"+e.getMessage());
            throw new EntpayException("根据银企支付单号下载转账回单文件异常"+e.getMessage(),e);
        }finally {
            if (zis != null){
                zis.close();
            }
            if (zio!=null){
                zio.close();
            }
        }

    }

    /**
     * 账单下载：通过时间范围查询并下载账单文件
     * @param
     * @return Range
     * @throws EntpayException
     */
    @PostMapping("/downloads/open-bank/accounts/{ent_acct_id}/file")
    public void billDownload (@PathVariable(value = "ent_acct_id") String ent_acct_id,@RequestBody ReceiptsDateRangeVo receiptsDateRangeVo,HttpServletResponse response) throws EntpayException, IOException {
        //对账单下载
        String ent_id = receiptsDateRangeVo.getEnt_id();
        String begin_date = receiptsDateRangeVo.getBegin_date();
        String end_date = receiptsDateRangeVo.getEnd_date();
        RequestOptions options = OptionUtils.getOp(ent_id);
        OpenBankStatementBillDownloadParam openBankStatementBillDownloadParam = OpenBankStatementBillDownloadParam.builder()
                .beginDate(begin_date)
                .endDate(end_date)
                .build();
        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        OpenBankFileDownload retrieveReceipt;
        FileDownloadResponse re;
        try {
            retrieveReceipt = OpenBankFileDownload.retrieveBankStatementBill(ent_acct_id,
                    openBankStatementBillDownloadParam, options);
            re = retrieveReceipt.download(options);
        } catch (EntpayException e) {
            ApiException apierror = (ApiException) e;
            log.error("通过时间范围查询并下载账单文件"+apierror.getError().getDesc()+e.getMessage());
            throw new EntpayException("通过时间范围查询并下载账单文件"+apierror.getError().getDesc()+e.getMessage());
        }
        try {

            if (retrieveReceipt.getFileStatus().equals(OpenBankFileDownload.FileStatusEnum.READY)) {
                InputStream inputStream = re.getInputStream();
                response.setContentType("application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("Receipt.PDF", "UTF-8"));
                bufferedOutputStream = new BufferedOutputStream(response.getOutputStream(),2048);
                bufferedInputStream = new BufferedInputStream(inputStream,2048);
                int len;
                while ((len = bufferedInputStream.read()) != -1){
                    bufferedOutputStream.write(len);
                }
            }else {
                throw new EntpayException("通过时间范围查询并下载账单文件");
            }
        }catch (IOException e) {
             log.error("通过时间范围查询并下载账单文件异常"+e.getMessage());
             throw new EntpayException("通过时间范围查询并下载账单文件异常"+e.getMessage(),e);
        } finally {
            if (bufferedInputStream != null){
                bufferedInputStream.close();
            }
            if (bufferedOutputStream != null){
                bufferedOutputStream.close();
            }
        }

    }

    @RequestMapping("/getOpenBankSuccess")
    public String cg(){
        log.info("企业申请成功回调");
        return "企业申请成功";
    }

    @RequestMapping("/getOpenBankError")
    public String yc(){
        log.info("企业申请异常回调");
        return "企业申请异常";
    }

    @RequestMapping("/aaa")
    public WebhookResult aaa(){
        return WebhookResult.success();
    }

}
