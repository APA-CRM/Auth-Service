package com.crm.auth.persistance.repository;

import com.crm.auth.persistance.entity.AccessControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessControlRepository extends JpaRepository<AccessControl, Long> {
}
