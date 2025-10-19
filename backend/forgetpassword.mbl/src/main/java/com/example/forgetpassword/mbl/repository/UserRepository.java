package com.example.forgetpassword.mbl.repository;

import com.example.forgetpassword.mbl.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// The primary key of UserEntity is now a String (the cnicNo)
public interface UserRepository extends JpaRepository<UserEntity, String> {

    // Custom query to find a user by their unique username
    Optional<UserEntity> findByUsername(String username);

    // Custom query to check if a username already exists
    boolean existsByUsername(String username);

    /**
     * Calls the sp_register_user stored procedure to create a new user.
     * @Modifying is crucial because this procedure performs an INSERT.
     */
    @Modifying
    @Procedure(procedureName = "sp_register_user")
    void registerUserProcedure(
            @Param("p_cnic_no") String cnicNo,
            @Param("p_username") String username,
            @Param("p_full_name") String fullName,
            @Param("p_hashed_password") String hashedPassword
    );

    /**
     * Calls the sp_reset_password stored procedure to update a user's password.
     * @Modifying is crucial because this procedure performs an UPDATE.
     */
    @Modifying
    @Procedure(procedureName = "sp_reset_password")
    void resetPasswordProcedure(
            @Param("p_cnic_no") String cnicNo,
            @Param("p_new_hashed_password") String newHashedPassword
    );

}