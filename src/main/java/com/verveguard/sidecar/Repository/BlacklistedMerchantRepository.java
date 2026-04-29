package com.verveguard.sidecar.Repository;

import com.verveguard.sidecar.Entity.BlacklistedMerchant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlacklistedMerchantRepository extends JpaRepository<BlacklistedMerchant, UUID> {


    @Cacheable(value = "blacklistCache", key = "#merchantId")

    boolean existsByMerchantId(String merchantId);

}
