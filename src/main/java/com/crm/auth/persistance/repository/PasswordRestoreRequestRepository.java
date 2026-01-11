package com.crm.auth.persistance.repository;

import com.crm.auth.persistance.entity.PasswordRestoreRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordRestoreRequestRepository
        extends JpaRepository<PasswordRestoreRequest, UUID> {

}
