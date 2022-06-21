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
@JsonTypeName("receiptsDateVo")
public class ReceiptsDateVo {
    @JsonProperty("ent_id")
    private String ent_id;
    @JsonProperty("query_date")
    private Date query_date;
}
