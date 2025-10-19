package com.example.forgetpassword.mbl.repository;

import com.example.forgetpassword.mbl.entity.AccountEntity;
import com.example.forgetpassword.mbl.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// The primary key of AccountEntity is now a String (the accountNumber).
public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    // Custom query to find an account by its number and ensure it belongs to the correct UserEntity.
    Optional<AccountEntity> findByAccountNumberAndUser(String accountNumber, UserEntity user);

    // Custom query to find an account directly by account number and the user's CNIC.
    @Query("SELECT a FROM AccountEntity a JOIN a.user u WHERE u.cnicNo = :cnicNo AND a.accountNumber = :accountNumber")
    Optional<AccountEntity> findByAccountNumberAndUserCnic(@Param("accountNumber") String accountNumber, @Param("cnicNo") String cnicNo);

    @Modifying(clearAutomatically = true)
    @Procedure(procedureName = "sp_create_account_for_existing_user")
    void createAccountForUser(
            @Param("p_cnic_no") String cnicNo,
            @Param("p_account_number") String accountNumber,
            @Param("p_account_title") String accountTitle,
            @Param("p_account_type") String accountType
    );
}