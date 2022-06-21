package com.example.openbank.serviceImp;

import com.example.openbank.dao.ProductOpenDao;
import com.example.openbank.mapper.ProductMapperTest;
import com.example.openbank.service.ProductServiceTest;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@SpringBootTest
@Slf4j
public class ProductServiceImpTest implements ProductServiceTest {
    @Autowired
    private ProductMapperTest productMapperTest;

}
