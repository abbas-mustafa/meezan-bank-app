package com.example.forgetpassword.mbl.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Data
@NoArgsConstructor
@Immutable // Marks this entity as read-only
@Table(name = "vw_account_summary") // Maps this entity to your SQL view
public class AccountSummaryView {

    @Id
    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "cnic_no")
    private String cnicNo;

    @Column(name = "username")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "account_title")
    private String accountTitle;
}