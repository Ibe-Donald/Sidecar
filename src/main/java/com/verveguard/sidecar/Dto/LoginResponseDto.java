package com.verveguard.sidecar.Dto;

import com.verveguard.sidecar.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String emailAddress;

    private String token;

    private Role role;
}
