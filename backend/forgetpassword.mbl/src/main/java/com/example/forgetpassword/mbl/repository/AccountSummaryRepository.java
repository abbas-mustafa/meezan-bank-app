package com.example.forgetpassword.mbl.repository;

import com.example.forgetpassword.mbl.entity.view.AccountSummaryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountSummaryRepository extends JpaRepository<AccountSummaryView, String> {

    // Spring Data JPA will automatically create the query:
    // "SELECT * FROM vw_account_summary WHERE cnic_no = ?"
    List<AccountSummaryView> findByCnicNo(String cnicNo);
}