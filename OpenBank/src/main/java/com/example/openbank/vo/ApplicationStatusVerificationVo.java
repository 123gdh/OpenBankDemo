package com.example.openbank.vo;

import com.example.openbank.result.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ApplicationStatusVerificationVo {
    private Result result;
    private String outRequestNo;
}
