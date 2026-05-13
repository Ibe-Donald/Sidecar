package com.verveguard.sidecar.Service;

import com.verveguard.sidecar.Dto.BlacklistedResponseDto;
import com.verveguard.sidecar.Entity.BlacklistedMerchant;
import com.verveguard.sidecar.Repository.BlacklistedMerchantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BlacklistedMerchantService {

    private final BlacklistedMerchantRepository blacklistedMerchantRepository;

    public BlacklistedMerchantService(BlacklistedMerchantRepository blacklistedMerchantRepository) {
        this.blacklistedMerchantRepository = blacklistedMerchantRepository;
    }

    // Get all BlacklistedMerchants
    public Page<BlacklistedResponseDto> fetchUsers(int pageNumber, int pageSize){

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<BlacklistedMerchant> merchantPage = blacklistedMerchantRepository.findAll(pageable);

        return merchantPage.map(merchant -> new BlacklistedResponseDto(
                merchant.getId(),
                merchant.getMerchantId(),
                merchant.getReason(),
                merchant.getCreatedAt()
        ));
    }
}
