package com.example.openbank.result;


import com.example.openbank.enums.Msg;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WebhookResult {
    @JsonProperty("retcode")
    public int retcode;
    @JsonProperty("retmsg")
    public String retmsg;
    public static WebhookResult success(){
        return new WebhookResult(0,"SUCCESS");
    }
}
