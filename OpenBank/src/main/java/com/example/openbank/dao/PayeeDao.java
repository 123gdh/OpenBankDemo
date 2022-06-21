package com.example.openbank.dao;

import com.tenpay.business.entpay.sdk.model.OpenBankTransferPayeeParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayeeDao{
    private String bank_name;
    private String out_payee_id;
    private String bank_account_name;
    private String bank_account_number;
    private String bank_branch_name;
    private String bank_branch_id;
}
