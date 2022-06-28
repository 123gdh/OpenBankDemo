package com.example.openbank.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayerDao {
    private String payer_id;
    private String ent_id;
    private String ent_name;
    private String ent_acct_id;
    private String bank_account_number_last4;
    private String ebank_no;
}
