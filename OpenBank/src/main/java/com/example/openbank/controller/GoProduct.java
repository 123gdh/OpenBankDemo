package com.example.openbank.controller;

import com.example.openbank.enums.*;
import com.example.openbank.enums.ResponseStatus;
import com.example.openbank.pojo.ObviousErrorList;
import com.example.openbank.result.Result;
import com.example.openbank.service.GoProductService;
import com.example.openbank.service.serviceImp.GoProductServiceImp;
import com.example.openbank.utils.CreateApiException;
import com.example.openbank.utils.ResultUtils;
import com.example.openbank.vo.ApplicationStatusVerificationVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpay.business.entpay.sdk.api.Enterprise;
import com.tenpay.business.entpay.sdk.api.OpenBankSupportBank;
import com.tenpay.business.entpay.sdk.api.ProductApplication;
import com.tenpay.business.entpay.sdk.common.ApiError;
import com.tenpay.business.entpay.sdk.exception.ApiException;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.*;
import com.tenpay.business.entpay.sdk.net.RequestOptions;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
@RestController
public class GoProduct {
    @Autowired
    private GoProductService service;

    @Autowired
    private GoProductServiceImp goProductServiceImp;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    //存储软证书：key为end_id，value为RequestOptions对象
    public static ConcurrentHashMap<String,RequestOptions> requestOptionsMap = new ConcurrentHashMap<String,RequestOptions>();

    /**
     * 申请产品开通
     * @param param
     * @return
     * @throws EntpayException
     */
    @PostMapping("/product-applications")
    public Result register(@RequestBody ProductApplicationParam param) throws EntpayException {
        List<Product> products = param.getProducts();
        if (products == null || products.size() <= 0){
            String error = "请选择需申请开通的产品";
            return ResultUtils.fail(ResponseStatus.ERROR.getdesc(),Msg.ERROR,error,Errordescription.OFFLINEEXCEPTION);
        }
        //根据社会统一信用代码+产品名称进行校验
        ApplicationStatusVerificationVo applicationStatusVerificationVo = null;
        try {
            applicationStatusVerificationVo = service.stateVerification(param);
        } catch (EntpayException e) {
            log.error("申请产品开通：状态校验异常: {}",e.getMessage());
            throw new EntpayException("申请产品开通：状态校验异常",e);
        }
        if (applicationStatusVerificationVo.getResult()!=null){
            return applicationStatusVerificationVo.getResult();
        }else if(StringUtils.hasText(applicationStatusVerificationVo.getOutRequestNo())){
            param.setOutRequestNo(applicationStatusVerificationVo.getOutRequestNo());
        }
        ProductApplication response;
        try {
            response = goProductServiceImp.createProcuct(param);
        } catch (ApiException e) {
            if (ObviousErrorList.goproductErroeList.contains(e.getError().getCode())) {
                service.updateStateByUnifiedPname(AccountDetail.StatusEnum.FAILED.getDesc(),param.getBusinessLicense().getUnifiedSocialCreditCode(),param.getProducts().get(0).getProductName().getDesc());
                log.error("申请产品开通：明确异常，申请产品开通失败: {}",e.getMessage());
                throw new EntpayException("申请产品开通：申请产品开通异常，申请失败",e);
            }else {
                log.error("申请产品开通：未知异常，请重新申请产品开通: {}",e.getMessage());
                throw new EntpayException("申请产品开通：申请产品开通异常，请重新申请开通",e);
            }
        }
        try {
            service.updateStateAndRequestNo(AccountDetail.StatusEnum.PROCESSING.getDesc(),response.getRequestNo(),param.getBusinessLicense().getUnifiedSocialCreditCode(),param.getProducts().get(0).getProductName().getDesc());
        } catch (Exception e) {
            log.error("更新产品开通状态异常：状态修改为PROCESSING异常: {}",e.getMessage());
            goProductServiceImp.retryUpdete(response,param);
        }
        Result result = null;
        try {
            result = openJump(response.getRequestNo());
        } catch (EntpayException e) {
            log.error("申请产品开通响应成功，但创建跳转链接失败，请重新调用创建跳转链接接口: {}",e.getMessage());
            throw new EntpayException("申请产品开通响应成功，但创建跳转链接失败，请重新调用创建跳转链接接口",e);
        }
        return result;
    }


    /**
     * 创建开通跳转url
     * @param request_no 银行支付申请编号
     * @return
     * @throws EntpayException
     */
    @PostMapping("/product-applications/{request_no}/links")
    public static Result openJump(@PathVariable(value = "request_no") String request_no) throws EntpayException {
        try {
            ProductApplication productApplication = new ProductApplication();
            productApplication.setRequestNo(request_no);
            AccountLink link = productApplication.createLink();
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,link);
        } catch (EntpayException e) {
            log.error("创建开通跳转链接异常: {}",e.getMessage());
            throw new EntpayException("创建开通跳转链接异常",e);
        }
    }

    /**
     * 查询产品开通：通过业务申请编号
     * @param out_request_no
     * @param httpServletResponse
     * @return
     * @throws EntpayException
     * @throws IOException
     */
    @GetMapping("/product-applications/out-request-no/{out_request_no}")
    public Result productLaunchDetailsByOutRequestNo(@PathVariable(value = "out_request_no") String out_request_no, HttpServletResponse httpServletResponse) throws EntpayException, IOException {
        try {
            ProductApplication application = new ProductApplication();
            if (StringUtils.hasText(out_request_no)) {
                application = ProductApplication.retrieveByOutRequestNo(out_request_no);
            }
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,application);
        } catch (EntpayException e) {
            log.error("申请产品开通：通过业务申请编号查询产品开通信息异常: {}",e.getMessage());
            throw new EntpayException("申请产品开通：通过业务申请编号查询产品开通信息异常",e);
        }
    }

    /**
     * 查询产品开通：通过银企付开户申请编号
     * @param request_no
     * @param httpServletResponse
     * @return
     */
    @GetMapping("/product-applications/{request_no}")
    public Result productLaunchDetailsByRequestNo(@PathVariable(value = "request_no") String request_no, HttpServletResponse httpServletResponse) throws EntpayException {
        try {
            ProductApplication application = new ProductApplication();
            if (StringUtils.hasText(request_no)) {
                application = ProductApplication.retrieve(request_no);
            }
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,application);
        } catch (EntpayException e) {
            log.error("通过业务申请编号查询产品开通信息异常: {}",e.getMessage());
            throw new EntpayException("通过业务申请编号查询产品开通信息异常",e);

        }
    }

    /**
     * 查询软证书
     * @param ent_id
     * @return
     * @throws EntpayException
     * @throws JsonProcessingException
     */
    @ResponseBody
    @GetMapping("/enterprises/{ent_id}/certificate")
    public Result getSoftCertificate(@PathVariable(value = "ent_id") String ent_id) throws EntpayException, JsonProcessingException {
        try {
            Certificate certificate = Enterprise.retrieveCertificate(ent_id);
            RequestOptions options = RequestOptions.getInstance()
                    .initOpenBank(certificate.getEntId(), certificate.getSerialNumber(), certificate.getEncryptedPrivateKey());
            if (!requestOptionsMap.containsKey(ent_id)){
                requestOptionsMap.put(ent_id,options);
            }
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,certificate);
        } catch (EntpayException e) {
            log.error("查询软件证书异常: {}",e.getMessage());
            throw new EntpayException("查询软件证书异常",e);
        }
    }

    /**
     * 查询企业开通账号
     * @param ent_id
     * @return
     * @throws EntpayException
     */
    @GetMapping("/enterprises/{ent_id}")
    public Result EnterpriseAccountOpening(@PathVariable(value = "ent_id") String ent_id) throws EntpayException {
        try {
            Enterprise response = Enterprise.retrieve(ent_id);
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,response);
        } catch (EntpayException e) {
            log.error("查询企业开通账号异常: {}",e.getMessage());
            throw new EntpayException("查询企业开通账号异常",e);
        }
    }

    /**
     * 查询支持银行列表
     * @return
     * @throws EntpayException
     */
    @GetMapping("/open-bank/supporting-banks")
    public Result getBanks() throws EntpayException {
        try {
            OpenBankSupportBankList response = OpenBankSupportBank.retrieve();
            return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(),Msg.SUCCESS,response);
        } catch (EntpayException e) {
            log.error("查询支持银行列表异常: {}",e.getMessage());
            throw new EntpayException("查询支持银行列表异常",e);
        }
    }

    /**
     * 申请成功回调方法
     * @return
     * @throws UnknownHostException
     */
    @RequestMapping("/getProductSuccess")
    public String cg() throws UnknownHostException {
        log.info("商企付开通申请成功");
        return "企业申请成功，回调到平台成功页面";
    }

    /**
     * 申请失败回调方法
     * @return
     */
    @RequestMapping("/getProductError")
    public String yc(){
        log.info("商企付开通申请失败");
        return "企业申请异常，回调到平台失败页面";
    }

}
