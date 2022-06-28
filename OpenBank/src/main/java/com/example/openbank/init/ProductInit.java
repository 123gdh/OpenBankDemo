package com.example.openbank.init;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tenpay.business.entpay.sdk.config.EntpayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@Slf4j
@ConfigurationProperties(prefix = "entpay")
@PropertySource(value = "classpath:application-plat.yml")
@JsonTypeName("ProductInit")
public class ProductInit {
        @Value("${platform-id}")
        private String platformId;

        @Value("${platform-private-key}")
        private String platformPrivateKey;

        @Value("${platform-private-cert-serial-no}")
        private String platformPrivateCertSerialNo;

        @Value("${tbep-serial-no}")
        private String tbepSerialNo;

        @Value("${tbep-public-key}")
        private String tbepPublicKey;

        @Value("${base-url}")
        private String baseUrl;

        @PostConstruct
        public void init() throws Exception {
            EntpayConfig.setBaseUrl(baseUrl);
            EntpayConfig.setNormalMode(
                    platformId,
                    platformPrivateKey,
                    platformPrivateCertSerialNo,
                    tbepSerialNo,
                    tbepPublicKey
            );
        }
}


