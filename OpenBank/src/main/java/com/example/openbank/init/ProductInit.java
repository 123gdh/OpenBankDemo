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

        @PostConstruct
        public void init() throws Exception {
            EntpayConfig.setBaseUrl("https://dev-api-business.tenpay.com");
//            EntpayConfig.setBaseUrl("https://dev-api.businesspay.qq.com");
            EntpayConfig.setNormalMode(
                    platformId,
                    "-----BEGIN RSA PRIVATE KEY-----\n" +
                            "    MIIEpAIBAAKCAQEA2Ir9OhahgHIJW3o651ZQWxLezJsc9KnJbotxU0yvJ6MWMLdy\n" +
                            "    5mvEYQ0Ub2ZRGnUCO0Lkdh69rWysOZWTfaMuXjrSvuGBr0oFpOIHT3zu8ySm+USr\n" +
                            "    xlq+SP5ynyqT5b0Uss2A5atGarWqccihw9cduyN4xsectN1Gqp8u6zQK9Xxsw8md\n" +
                            "    8rKxNL1Qi0A3N2cz2iBVqCdlU70rCtsQWaea6o2K1amXkxyUG+dshic6XvIi7hJ5\n" +
                            "    pLOVgTyOA9fSzgYW1xnIgN+byoy1LFaOrp+SYnIrIhrundtZLvGuRBSK6aDCYLZi\n" +
                            "    AcgSJaXZUeHGnSCdl15ttU1M7Ns6ADmq9Gu26wIDAQABAoIBAQCjPZwKN1dGmjEd\n" +
                            "    qfZPwQzQq1gUTHa2pmw4jpw+3JdKPTKURSc0N1eShia6mBpZ5I1rGVFbDm+VFlB7\n" +
                            "    FJxcYTCqTpPi2+NEvnukwdQbAyarWx0Y2sDXxj6ejlrgTFu0HCdHZ45dFRH/jbXR\n" +
                            "    ExuHpFpAYkxBJeennQwRwfC/1LrSNbdu3excY6RqAhK9jv1KFBPQm1xKNvG/gTIg\n" +
                            "    6dKxcEVm1FQQvt8zbNC9X4Rlov/B4JbTmJbqVUdCOOjyTexTz8icZaIOqLrb6EF7\n" +
                            "    YoL3iWH2gyG1L2IfTksLRVkV7axP8HSttsmIwk13g5vxYf9EyoGOba13RTO1Pe9o\n" +
                            "    P4FmP/RpAoGBAPzrcdLVbcjKF7s0RDabMD14KklZdrC2/k9PZcbZN4J1WiCYzrC5\n" +
                            "    6zJdH3wQcYs5Wx+IUwyPLcN3s29MVKfk3u4ibdd2BOPI4uSuJLbZGY0LICcZnAJV\n" +
                            "    gWseUArsfmqmgvcKjTIfz649oOziL5Yo1TcyPsSle9kEP/KfPxov66BNAoGBANsu\n" +
                            "    IPHAmwHkNhSaciJ7fZgel+slnoNNH9m/QwGdezSyhaNdeqm7/oCR6WNbb8a9VUPD\n" +
                            "    WjHLwBsck82/QSO9nwosqYD7dfQYTSoPUUntyEFlqFSrJyI4W8h9ZHd5BmKuxues\n" +
                            "    mAGGVAgudqnzUNU00K5Oc+OMJ2QHmuhV+S8O6ZAXAoGAalYLOxtk1wCwzZKCA3tn\n" +
                            "    4tA85y3oCxBFB89X+SymfAEe7YmRjRf93OTuMdkwA9sPhshoMmWYVhBjrq2nAhfl\n" +
                            "    6rUq/WWvVJ3HV2IoKYBJj9VXpVjNEOYmTYUnHhCkbe6oLXmQN7zMMnPSzwWiAD4W\n" +
                            "    tug3H7J7yZhR0o5Fl5sCNBkCgYEA0nKyMy2qJ+DFhHbfC/jimMMXPzL5xjyxTlP0\n" +
                            "    tIJPIEQhSMCi3IKjVTPEkh/jbUGxssTJv3JRXDbOPAldNEIHiK9leXD4yozi2nWm\n" +
                            "    qYNkelZvkAxeIN9YUWMEqipMVUhXt27i7OPbn5dtXLVBcdWqYRL29w9FayhaNqcG\n" +
                            "    AYch/7UCgYBKp8etY8fOfbbY8t2YSsAcxFDW78yFUalT7cSSfn0huAcajDFFrQG7\n" +
                            "    fu4pynE5+6uWQiwU3/LqlL7/apALKOiaa9WnFdv0qrgR1x+9opvLLecKliSvjQLn\n" +
                            "    lGQycYtZeGbMYXDRg3rGvkvLvPAPfUqroboGdzuiSYfwmptGjwFgYA==\n" +
                            "    -----END RSA PRIVATE KEY-----",
                    platformPrivateCertSerialNo,
                    tbepSerialNo,
                    "-----BEGIN RSA PUBLIC KEY-----\n" +
                            "    MIIBCgKCAQEAubADF7+Tr1WSQQQkeCq4qT/TIt1dxNj5LdIEVCZhYBHuA/cgEEAY\n" +
                            "    HsWZutmAFx9w8PAQBDEkK+W9/0f+KYafjgFHplGiStdTTUq3qJKeS4lVK9RTXp+7\n" +
                            "    o9wYQSFk0wAQu56SxRs7NIBtF17eUQqUU/bLqa9JiVgh2zEFKgOpC+M504RKAP1O\n" +
                            "    LeHL9wYjll88z8w40TytLjWXR5tuEfjYJlebI2AbNwdcChXKcnM7/08xq2UltBQ+\n" +
                            "    qn6a+FyITpg2A7m+M6xp6PAiV2/Xx4P4EW5Pr6mwWBKaYm21R6cjhPRg0qdQkOPW\n" +
                            "    NGfNJM01wKMOmCrTERH3+BrIeOXz5symvQIDAQAB\n" +
                            "    -----END RSA PUBLIC KEY-----"
            );

        }

}


