package com.example.forgetpassword.mbl.dto.account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlowVerificationRequestDTO {

    private String cnicNo;

    private String accountNumber;

    private String newPassword;

}