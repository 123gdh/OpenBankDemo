package com.example.openbank.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpenBankDao {
    private String out_application_id;
    private String bank_account_name;
    private String bank_account_number;
    private String bank_abbreviation;
    private String application_id;
    private String ent_id;
    private String status;
    private String application_type;
    private String sign_type;
    private String bank_account_number_last4;
    private String bank_name;
    private String failed_type;
    private String failed_detail;
    private String error_code;
    private String error_state;
    private String error_msg;
    private Date create_date;
}
