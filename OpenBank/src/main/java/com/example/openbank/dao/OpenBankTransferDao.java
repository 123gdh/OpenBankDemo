package com.example.openbank.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpenBankTransferDao{
    private String out_transfer_id;
    private String transfer_id;
    private String payer_id;
    private String payee_id;
    private String transfer_status;
    private long amount;
    private String currency;
    private String memo;
    private String attachment;
    private String goods_name;
    private String goods_detail;
    private String receipt_status;
    private String not_supported_reason;
    private int receipt_eta;
    private String pc_guide_url;
    private String mobile_guide_url;
    private String failed_type;
    private String failed_detail;
}
