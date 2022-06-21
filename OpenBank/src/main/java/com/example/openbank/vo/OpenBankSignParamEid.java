package com.example.openbank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tenpay.business.entpay.sdk.model.OpenBankSignParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("OpenBankSignParamEid")
public class OpenBankSignParamEid {
    @JsonProperty("OpenBankSignParam")
    private OpenBankSignParam openBankSignParam;
    @JsonProperty("ent_id")
    private String ent_id;
}
