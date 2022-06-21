package com.example.openbank;

import com.example.openbank.mapper.ProductMapperTest;
import com.example.openbank.service.ProductServiceTest;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = OpenBankApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class TransactionlTest {
//    @Autowired
//    private ProductServiceTest productServiceTest;

    @Autowired
    private ProductMapperTest productMapperTest;

    @Transactional
    @Rollback(value = false)
    @Test
    public void transcationTest() throws EntpayException{
        try {
            productMapperTest.updateProductByoutRequestNo("12345","1234567");
            int a = 1/0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EntpayException("update出现异常");
        }
//            productServiceTest.updateProductByoutRequestNo("123","1234567");

        System.out.println("正常运行");
    }

}
