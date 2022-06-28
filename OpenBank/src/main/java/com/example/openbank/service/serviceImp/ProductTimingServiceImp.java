package com.example.openbank.service.serviceImp;

import com.example.openbank.dao.ProductTimingDao;
import com.example.openbank.mapper.ProductTimingMapper;
import com.example.openbank.service.ProductTimingService;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductTimingServiceImp implements ProductTimingService {
    @Autowired
    private ProductTimingMapper productTimingMapper;

    @Override
    public List<ProductTimingDao> queryAll()throws EntpayException{
        try {
            return productTimingMapper.queryAll();
        } catch (Exception e) {
            log.error("申请产品开通：查询定时查询申请单号列表异常"+e.getMessage());
            throw new EntpayException("申请产品开通：查询定时查询申请单号列表异常"+e.getMessage(),e);
        }
    }

    @Override
    public ProductTimingDao queryByOutRequestNo(String out_request_no)throws EntpayException {
        try {
            return productTimingMapper.queryByOutRequestNo(out_request_no);
        } catch (Exception e) {
            log.error("申请产品开通：通过业务申请单号查询定时查询单号异常"+e.getMessage());
            throw new EntpayException("申请产品开通：通过业务申请单号查询定时查询单号异常"+e.getMessage(),e);
        }
    }

    @Override
    public void deleteProductTiming(String out_request_no) throws EntpayException{
        try {
            productTimingMapper.deleteProductTiming(out_request_no);
        } catch (Exception e) {
            log.error("申请产品开通：根据业务申请单号删除定时查询记录异常"+e.getMessage());
            throw new EntpayException("申请产品开通：根据业务申请单号删除定时查询记录异常"+e.getMessage(),e);
        }
    }

    @Override
    public void insertProductTiming(ProductTimingDao productTimingDao) throws EntpayException {
        try {
            productTimingMapper.insertProductTiming(productTimingDao);
        } catch (Exception e) {
            log.error("申请产品开通：添加定时查询业务申请单号异常"+e.getMessage());
            throw new EntpayException("申请产品开通：添加定时查询业务申请单号异常"+e.getMessage(),e);
        }
    }

    public void retryInsert(String out_request_no)throws EntpayException{
        int len = 3;
        for (int i = 1; i <= len; i++) {
            try {
                ProductTimingDao productTimingDao = queryByOutRequestNo(out_request_no);
                if (productTimingDao!=null){
                    log.info("申请产品开通：添加定时任务申请单out_request_no为"+out_request_no+"的申请单,尝试重新添加定时执行单：成功");
                    continue;
                }
                insertProductTiming(new ProductTimingDao(out_request_no));
                i--;
            } catch (EntpayException entpayException) {
                log.error("申请产品开通：添加定时任务申请单out_request_no为"+out_request_no+"的申请单异常,尝试重新添加定时执行单，第"+i+"次重试: {}",entpayException.getMessage());
                if (i == len){
                    log.error("申请产品开通：添加定时任务申请单out_request_no为"+out_request_no+"的申请单异常,重新添加定时执行单: 失败 {}",entpayException.getMessage());
                    throw new EntpayException("申请产品开通：添加定时任务申请单out_request_no为"+out_request_no+"异常,重新添加定时执行单：失败",entpayException);
                }
            }
        }
    }
}
