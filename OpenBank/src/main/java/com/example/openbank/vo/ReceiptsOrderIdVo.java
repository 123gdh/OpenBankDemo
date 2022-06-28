package com.example.openbank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonTypeName("receiptsOrderIdVo")
public class ReceiptsOrderIdVo {
    @JsonProperty("ent_id")
    private String ent_id;
    @JsonProperty("order_id")
    private String order_id;
    @JsonProperty("trade_type")
    private String trade_type;
}
