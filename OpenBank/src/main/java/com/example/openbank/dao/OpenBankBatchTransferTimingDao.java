package com.example.openbank.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpenBankBatchTransferTimingDao {
    private String out_batch_transfer_id;
    private String ent_id;
}
