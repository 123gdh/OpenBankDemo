package com.example.openbank.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductOpenDao {
    private String out_request_no;
    private String request_no;
    private String status;
    private String unified_social_credit_code;
    private String merchant_name;
    private String product_name;
    private Date create_date;
}
