package com.example.openbank.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObviousErrorList {
    public final static List<String> goproductErroeList = new ArrayList<>(Arrays.asList("ENT_NAME_NOT_MATCH_CODE", "ENT_REQUEST_LIMITED", "PLATFORM_REQUEST_LIMITED","REGISTER_NOT_FINISHED","PRODUCT_OPEN_SUCCEED"));
    public final static List<String> openbankErroeList = new ArrayList<>(Arrays.asList("PLATFORM_NOT_SUPPORT_BANK", "ACCOUNT_NAME_NOT_MATCH_ENT_NAME", "REENTRY_DATA_CHECK_FAILED","SIGN_PROCESSING","SIGN_SUCCESSED","SIGN_FAILED"));
}
