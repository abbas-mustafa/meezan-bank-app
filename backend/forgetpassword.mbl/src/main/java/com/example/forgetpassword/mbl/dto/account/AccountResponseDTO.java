package com.example.forgetpassword.mbl.dto.account;

import com.example.forgetpassword.mbl.entity.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {

    private String accountNumber;
    private String accountType;
    private String iban;
    private String accountTitle;
    private String status;
    private String userCnicNo;

    // Helper method to easily convert an Entity to this DTO
    public static AccountResponseDTO fromEntity(AccountEntity entity) {
        return AccountResponseDTO.builder()
                .accountNumber(entity.getAccountNumber())
                .accountType(entity.getAccountType().name())
                .iban(entity.getIban())
                .accountTitle(entity.getAccountTitle())
                .status(entity.getStatus())
                .userCnicNo(entity.getUser().getCnicNo()) // Extracts just the CNIC
                .build();
    }
}