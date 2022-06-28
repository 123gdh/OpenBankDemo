package com.example.openbank.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransfersDao {
    private String transfer_id;
    private String out_transfer_detail_id;
    private long amount;
    private String memo;
    private String payee_id;
    private String receipt_status;
    private String not_supported_reason;
    private int receipt_eta;
    private String failed_type;
    private String failed_detail;
    private String transfer_status;
}
