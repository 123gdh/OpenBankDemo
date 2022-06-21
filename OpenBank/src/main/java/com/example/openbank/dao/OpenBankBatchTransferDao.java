package com.example.openbank.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpenBankBatchTransferDao {
    private String batch_transfer_id;
    private String out_batch_transfer_id;
    private long total_amount;
    private String currency;
    private int total_num;
    private String payer_id;
    private String batch_transfer_status;
    private String transfers;
    private String batch_memo;
    private String attachment;
    private String goods_name;
    private String goods_detail;
    private int succeeded_transfer_num;
    private String private_transfer_type;
    private String pc_guide_url;
    private String mobile_guide_url;
}
