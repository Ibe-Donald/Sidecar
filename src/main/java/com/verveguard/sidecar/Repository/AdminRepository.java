package com.verveguard.sidecar.Repository;

import com.verveguard.sidecar.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

    boolean existsByEmailAddress(String emailAddress);

    Optional<Admin> findByEmailAddress(String emailAddress);

}
