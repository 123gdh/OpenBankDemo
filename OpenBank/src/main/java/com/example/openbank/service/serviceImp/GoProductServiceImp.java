package com.example.openbank.service.serviceImp;

import com.example.openbank.controller.GoProduct;
import com.example.openbank.dao.ProductOpenDao;
import com.example.openbank.dao.ProductTimingDao;
import com.example.openbank.enums.Msg;
import com.example.openbank.enums.ResponseStatus;
import com.example.openbank.mapper.GoProductMapper;
import com.example.openbank.result.Result;
import com.example.openbank.service.GoProductService;
import com.example.openbank.service.ProductTimingService;
import com.example.openbank.utils.ResultUtils;
import com.example.openbank.vo.ApplicationStatusVerificationVo;
import com.tenpay.business.entpay.sdk.api.ProductApplication;
import com.tenpay.business.entpay.sdk.exception.ApiException;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service("goProductServiceImp")
@Slf4j
@Transactional(rollbackFor = EntpayException.class)
public class GoProductServiceImp implements GoProductService {

    @Autowired
    private GoProductMapper goProductMapper;

    @Autowired
    private ProductTimingService productTimingService;

    @Autowired
    private ProductTimingServiceImp productTimingServiceImp;

    @Override
    public void insertProduct(ProductOpenDao productOpenDao) throws EntpayException{
        try {
            goProductMapper.insertProduct(productOpenDao);
        } catch (Exception e) {
            log.error("存储产品申请单异常"+e.getMessage());
            throw new EntpayException("存储产品申请单异常"+e.getMessage(),e);
        }
    }

    @Override
    public ProductOpenDao queryProduct(String unified_social_credit_code, String product_name) throws EntpayException{
        try {
            return  goProductMapper.queryProduct(unified_social_credit_code, product_name);
        } catch (Exception e) {
            log.error("根据社会统一信用代码及产品名称查询异常"+e.getMessage());
            throw new EntpayException("根据社会统一信用代码及产品名称查询异常"+e.getMessage(),e);
        }
    }

    @Override
    public List<ProductOpenDao> queryOverdueApplication(Date date)throws EntpayException {
        try {
            return goProductMapper.queryOverdueApplication(date);
        } catch (Exception e) {
            log.error("获取申请超过十天过期的申请单异常"+e.getMessage());
            throw new EntpayException("获取申请超过十天过期的申请单异常"+e.getMessage(),e);
        }
    }

    @Override
    public ProductOpenDao queryByOutRequestNo(String out_request_no) throws EntpayException {
        try {
            return goProductMapper.queryByOutRequestNo(out_request_no);
        } catch (Exception e) {
            log.error("通过业务申请单号查询产品申请单异常"+e.getMessage());
            throw new EntpayException("通过业务申请单号查询产品申请单异常"+e.getMessage(),e);
        }
    }

    /**
     * 状态校验
     * @param param
     * @return
     * @throws EntpayException
     */
    @Override
    public ApplicationStatusVerificationVo stateVerification(ProductApplicationParam param)throws EntpayException{
        try {
            ProductOpenDao   productOpenDao = queryProduct(param.getBusinessLicense().getUnifiedSocialCreditCode(), param.getProducts().get(0).getProductName().getDesc());
            Result result = null;
            String outRequestNo = null;
            if (productOpenDao != null) {
                String desc = productOpenDao.getStatus();
                if (desc.equals(AccountDetail.StatusEnum.PROCESSING.getDesc())) {
                    result = GoProduct.openJump(productOpenDao.getRequest_no());
                }
                if (desc.equals(AccountDetail.StatusEnum.SUCCESS.getDesc())) {
                    String adopt = "该产品已开通成功";
                    result = ResultUtils.success(ResponseStatus.SUCCESS.getdesc(), Msg.SUCCESS, adopt);
                }
                if (desc.equals(AccountDetail.StatusEnum.FAILED.getDesc())){
                    outRequestNo = UUID.randomUUID().toString();
                    productOpenDao = new ProductOpenDao(outRequestNo,null,AccountDetail.StatusEnum.INIT.getDesc(),param.getBusinessLicense().getUnifiedSocialCreditCode(),param.getBusinessLicense().getMerchantName(),param.getProducts().get(0).getProductName().getDesc(),new Date(System.currentTimeMillis()));
                    insertProduct(productOpenDao);
                }
                if(desc.equals(AccountDetail.StatusEnum.INIT.getDesc())){
                    outRequestNo = productOpenDao.getOut_request_no();
                }
            }else {
                outRequestNo = UUID.randomUUID().toString();
                productOpenDao = new ProductOpenDao(outRequestNo, null, AccountDetail.StatusEnum.INIT.getDesc(), param.getBusinessLicense().getUnifiedSocialCreditCode(), param.getBusinessLicense().getMerchantName(), param.getProducts().get(0).getProductName().getDesc(), new Date(System.currentTimeMillis()));
                insertProduct(productOpenDao);
            }
            return new ApplicationStatusVerificationVo(result,outRequestNo);
        } catch (EntpayException e) {
            throw new EntpayException(e);
        }
    }

    @Override
    public void updateStateByUnifiedPname(String status,String unified_social_credit_code, String product_name)throws EntpayException {
        try {
            goProductMapper.updateStateByUnifiedPname(status,unified_social_credit_code,product_name);
        } catch (Exception e) {
            log.error("产品开通更新PROCESSING状态异常"+e.getMessage());
            throw new EntpayException("产品开通更新PROCESSING状态异常"+e.getMessage(),e);
        }
    }

    @Override
    public void updateStateAndRequestNo(String status, String request_no, String unified_social_credit_code, String product_name) throws EntpayException {
        try {
            goProductMapper.updateStateAndRequestNoByUnifiedPname(status,request_no,unified_social_credit_code,product_name);
        } catch (Exception e) {
            log.error("申请产品开通：添加银企付申请单号及修改产品开通状态为PROCESSING"+e.getMessage());
            throw new EntpayException("申请产品开通：添加银企付申请单号及修改产品开通状态为PROCESSING"+e.getMessage(),e);
        }
    }


    @Override
    public void updateStateByOutRequestNo(String status, String out_request_no) throws EntpayException {
        try {
            goProductMapper.updateStateByOutRequestNo(status,out_request_no);
        } catch (Exception e) {
            log.error("产品开通update：以out_request_no作为条件修改状态异常"+e.getMessage());
            throw new EntpayException("产品开通update：以out_request_no作为条件修改状态异常"+e.getMessage(),e);
        }
    }

    /**
     * 发起申请产品开通
     * @param param
     * @return
     * @throws EntpayException
     */
    @Transactional(rollbackFor = EntpayException.class)
    public ProductApplication createProcuct(ProductApplicationParam param) throws EntpayException {
        //存储需定时查询数据单号
        try {
            productTimingService.insertProductTiming(new ProductTimingDao(param.getOutRequestNo()));
        } catch (EntpayException e) {
            //重试策略
            try {
                productTimingServiceImp.retryInsert(param.getOutRequestNo());
            } catch (EntpayException entpayException) {
                log.error("申请签约：将签约申请数据存储到定时查询表异常: {}",e.getMessage());
                throw new EntpayException("申请产品开通：状态校验异常",e);
            }
        }
        List<Product> products = param.getProducts();
        BusinessLicense business_license = param.getBusinessLicense();
        //产品名称
        //法人身份证信息
        LegalPersonIdCard legalPersonIdCard = param.getLegalPersonIdCard();
        String hostAddress;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("申请产品开通获取本地IP异常"+e.getMessage(),e);
            hostAddress = "localhost";
        }
        NotifyUrl notifyUrl = NotifyUrl.builder()
                .serverNotifyUrl("http://10.43.26.46:8080/Callback/product-application")
                .webSuccessUrl("http://"+hostAddress+":8080/getProductSuccess")
                .webRefreshUrl("http://"+hostAddress+":8080/getProductError")
                .build();
        ProductApplicationParam accountApplicationParam = ProductApplicationParam.builder()
                .businessLicense(business_license)
                .products(Collections.singletonList(products.get(0)))
                .notifyUrl(notifyUrl)
                .outRequestNo(param.getOutRequestNo())
                .legalPersonIdCard(legalPersonIdCard)
                .build();
        ProductApplication productApplication;
        try {
            productApplication = ProductApplication.create(accountApplicationParam);
        } catch (ApiException e) {
            log.error("申请产品开通：调用银企付申请产品开通接口异常: {}",e.getMessage());
            throw e;
        }
        return productApplication;
    }

    /**
     * 申请产品开通状态修改重试策略
     * @param response
     * @param param
     */
    public void retryUpdete(ProductApplication response,ProductApplicationParam param)throws EntpayException{
        int len = 3;
        for (int i = 1; i <= len; i++) {
            try {
                ProductOpenDao productOpenDao = queryProduct(param.getBusinessLicense().getUnifiedSocialCreditCode(), param.getProducts().get(0).getProductName().getDesc());
                if (!productOpenDao.getStatus().equals(AccountDetail.StatusEnum.INIT.getDesc())){
                    log.info("申请产品开通：申请单号为"+param.getOutRequestNo()+"的申请单修改申请产品开通状态为PROCESSING，重试修改状态：成功");
                    continue;
                }
                    updateStateAndRequestNo(AccountDetail.StatusEnum.PROCESSING.getDesc(), response.getRequestNo(), param.getBusinessLicense().getUnifiedSocialCreditCode(), param.getProducts().get(0).getProductName().getDesc());
                    i--;
            } catch (EntpayException entpayException) {
                log.error("申请产品开通：申请单号为"+param.getOutRequestNo()+"的申请单修改申请产品开通状态为PROCESSING发送异常，尝试重试修改状态，第"+i+"次重试 :{}",entpayException.getMessage());
                if (i == len){
                    log.error("申请产品开通：申请单号为"+param.getOutRequestNo()+"的申请单修改申请产品开通状态为PROCESSING发送异常，重试修改状态：失败 :{}",entpayException.getMessage());
                    throw new EntpayException("申请产品开通：申请单号为"+param.getOutRequestNo()+"的申请单修改申请产品开通状态为PROCESSING发送异常，重试修改状态：失败",entpayException);
                }
            }
        }
    }

}
