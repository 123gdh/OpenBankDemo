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
@JsonTypeName("receiptsTokenVo")
public class ReceiptsTokenVo {
    @JsonProperty("ent_id")
    private String ent_id;
    @JsonProperty("token")
    private String token;
}
