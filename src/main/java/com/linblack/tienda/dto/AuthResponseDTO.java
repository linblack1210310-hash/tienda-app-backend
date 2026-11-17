package com.linblack.tienda.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String jwt;
    private String username;
    private String email;
}
