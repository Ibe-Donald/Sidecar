package com.verveguard.sidecar.Dto;

import com.verveguard.sidecar.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDto {

    private String name;

    private String emailAddress;

    private Role role;

}
