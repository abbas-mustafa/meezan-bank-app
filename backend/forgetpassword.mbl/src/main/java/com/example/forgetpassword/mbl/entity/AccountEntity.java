package com.example.forgetpassword.mbl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountEntity {

    @Id
    @Column(name = "account_number", nullable = false, unique = true, length = 14)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountClassification accountType;

    @Column(name = "iban", unique = true, length = 24)
    private String iban;

    @Column(name = "account_title", unique = true ,nullable = false, length = 100)
    private String accountTitle;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_cnic_no", referencedColumnName = "cnic_no", nullable = false)
    private UserEntity user;

    public enum AccountClassification {
        INDIVIDUAL,
        SOLE_PROPRIETOR,
        FOREIGN_NATIONAL
    }
}