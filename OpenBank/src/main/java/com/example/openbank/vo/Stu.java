package com.example.openbank.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tenpay.business.entpay.sdk.model.RetrieveStatementGetParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonTypeName("stu")
public class Stu implements Serializable {
    @JsonProperty("ent_acct_id")
    private String ent_acct_id;
    @JsonProperty("RetrieveStatementGetParam")
    private com.tenpay.business.entpay.sdk.model.RetrieveStatementGetParam RetrieveStatementGetParam;
}
