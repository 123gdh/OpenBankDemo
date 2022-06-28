package com.example.openbank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonTypeName("receiptsDateRangeVo")
public class ReceiptsDateRangeVo {
    @JsonProperty("ent_id")
    private String ent_id;
    @JsonProperty("begin_date")
    private String begin_date;
    @JsonProperty("end_date")
    private String end_date;
}
