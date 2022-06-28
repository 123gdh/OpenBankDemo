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
public class Transfers {
    private String transfer_id;
    private String out_transfer_detail_id;
    private BigDecimal amount;
    private String memo;
    private String payee;
}
