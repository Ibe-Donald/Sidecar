package com.verveguard.sidecar.Dto;

import com.verveguard.sidecar.Enum.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "An email address is required")
    @Email(message = "A valid email address is required")
    private String emailAddress;

    @NotBlank(message = "Enter your password")
    private String password;

    @NotNull(message = "Your role cannot be null")
    private Role role;

}
