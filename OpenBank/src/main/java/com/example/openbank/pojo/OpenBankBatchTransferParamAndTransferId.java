package com.example.openbank.pojo;

import com.tenpay.business.entpay.sdk.model.OpenBankBatchTransferParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpenBankBatchTransferParamAndTransferId {
    private OpenBankBatchTransferParam openBankBatchTransferParam;
    private String transferId;
}
