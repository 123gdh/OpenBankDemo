package com.example.openbank.controller;

import com.example.openbank.enums.Msg;
import com.example.openbank.enums.ResponseStatus;
import com.example.openbank.result.Result;
import com.example.openbank.utils.ResultUtils;
import com.example.openbank.timing.ScheduledTask;
import com.tenpay.business.entpay.sdk.api.OpenBankSupportBank;
import com.tenpay.business.entpay.sdk.exception.EntpayException;
import com.tenpay.business.entpay.sdk.model.BankCollection;
import com.tenpay.business.entpay.sdk.model.OpenBankSupportBankList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/getBank")
@Slf4j
@RestController
public class GetBank {
    @RequestMapping("/open-bank/supporting-banks")
    public static Result getBanklist() throws EntpayException {
        if (ScheduledTask.map == null){
            OpenBankSupportBankList response = OpenBankSupportBank.retrieve();
            List<OpenBankSupportBank> bankList = response.getBanks();
            for (int a = 0;a<bankList.size();a++){
                OpenBankSupportBank bank = bankList.get(a);
                String bankName = bank.getBankName();
                ScheduledTask.map.put(bankName,bank);
            }
        }
        return ResultUtils.success(ResponseStatus.SUCCESS.getdesc(), Msg.SUCCESS, ScheduledTask.map);
    }

}
