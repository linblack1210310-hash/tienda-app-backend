package com.linblack.tienda.controller;

import com.linblack.tienda.dto.AuthRequestDTO;
import com.linblack.tienda.dto.AuthResponseDTO;
import com.linblack.tienda.dto.HttpGlobalResponseDTO;
import com.linblack.tienda.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<HttpGlobalResponseDTO<AuthResponseDTO>> login(@RequestBody AuthRequestDTO request) {
        try {
            AuthResponseDTO data = authService.login(request);
            return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Login exitoso", data));
        } catch (Exception e) {
            // Aquí atrapamos el error y devolvemos 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HttpGlobalResponseDTO<>(e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<HttpGlobalResponseDTO<AuthResponseDTO>> register(@RequestBody AuthRequestDTO request) {
        try {
            AuthResponseDTO data = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new HttpGlobalResponseDTO<>("Usuario registrado con éxito", data));
        } catch (Exception e) {
            // Aquí devolvemos 409 Conflict por duplicados o 400 por error de datos
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new HttpGlobalResponseDTO<>(e.getMessage(), null));
        }
    }
}
