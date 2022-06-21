package com.example.openbank;

import com.example.openbank.dao.ProductOpenDao;
import com.example.openbank.mapper.ProductMapperTest;
import com.tenpay.business.entpay.sdk.model.VoucherTradeTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sun.dc.pr.PRError;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

//@SpringBootTest
public class IfTest {

    @Autowired
    private ProductMapperTest productMapperTest;
    @Test
    public void dateTest(){
        Calendar today = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"));
//        today.setTimeInMillis(new Date().getTime());
//        today.add(Calendar.DAY_OF_MONTH, -10);
        today.add(Calendar.YEAR, -1);
        System.out.println("中文乱码");
        System.out.println("当前时间"+new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss+08:00").format(today.getTime()));

        System.out.println(new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss+08:00").format(new Date()));

        LocalDate to = LocalDate.now();
        LocalDate previousYear = to.minus(1, ChronoUnit.YEARS);

        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.YEAR,-1);
        String format = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        System.out.println(format);
        System.out.println(System.currentTimeMillis());
    }

}
