package com.example.forgetpassword.mbl.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany; // ADDED: Import List
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity {

    @Id
    @Column(name = "cnic_no", nullable = false, unique = true, length = 13)
    private String cnicNo;

    @Column(name = "full_name")
    private String name;

    @Column(name = "role")
    private String role = "USER";

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "hashed_password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<AccountEntity> accounts;
}