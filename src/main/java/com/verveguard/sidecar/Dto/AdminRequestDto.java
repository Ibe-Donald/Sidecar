package com.verveguard.sidecar.Dto;

import com.verveguard.sidecar.Enum.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Enter a valid email address")
    private String emailAddress;

    @NotBlank(message = "A password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message = "Role must be specified")
    private Role role;

}
