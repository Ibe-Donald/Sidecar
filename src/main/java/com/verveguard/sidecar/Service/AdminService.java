package com.verveguard.sidecar.Service;

import com.verveguard.sidecar.Dto.AdminRequestDto;
import com.verveguard.sidecar.Dto.AdminResponseDto;
import com.verveguard.sidecar.Dto.LoginRequestDto;
import com.verveguard.sidecar.Dto.LoginResponseDto;
import com.verveguard.sidecar.Entity.Admin;
import com.verveguard.sidecar.Repository.AdminRepository;
import com.verveguard.sidecar.exception.PasswordException;
import com.verveguard.sidecar.exception.UserEmailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Entity -> ResponseDto
    public AdminResponseDto convertAdminDto(Admin admin){

        AdminResponseDto dto = new AdminResponseDto();

        dto.setName(admin.getName());
        dto.setEmailAddress(admin.getEmailAddress());
        dto.setRole(admin.getRole());

        return dto;
    }


    public AdminResponseDto createAdmin(AdminRequestDto dto){
        if(adminRepository.existsByEmailAddress(dto.getEmailAddress())){
            throw new UserEmailException("User with this email " + dto.getEmailAddress() + " exists.");
        }

        Admin admin = new Admin();

        admin.setName(dto.getName());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setEmailAddress(dto.getEmailAddress());
        admin.setRole(dto.getRole());

        adminRepository.save(admin);

        return convertAdminDto(admin);
    }

    public LoginResponseDto adminLogin(LoginRequestDto dto){

        Admin admin = adminRepository.findByEmailAddress(dto.getEmailAddress())
                .orElseThrow(() -> new UserEmailException("The email provided doesn't exist in our system"));

        boolean matches = passwordEncoder.matches(dto.getPassword(), admin.getPassword());

        if (!matches){
            throw new PasswordException("Invalid password");
        }

        String token = jwtService.generateToken(admin.getEmailAddress(), String.valueOf(admin.getRole()));

        return LoginResponseDto.builder()
                .emailAddress(admin.getEmailAddress())
                .token(token)
                .role(admin.getRole())
                .build();

    }
}
