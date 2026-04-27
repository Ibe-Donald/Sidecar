package com.verveguard.sidecar.merchant;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlacklistedMerchantRepository extends JpaRepository<BlacklistedMerchant, UUID> {


     // Checks if a merchant exists in the database by their natural String ID.
     // If the same merchant ID is checked twice, the database is completely bypassed
     // on the second attempt, drops response time

    @Cacheable(value = "blacklistCache", key = "#merchantId")

    boolean existsByMerchantId(String merchantId);

}
