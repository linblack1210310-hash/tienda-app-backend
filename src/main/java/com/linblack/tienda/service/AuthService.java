package com.linblack.tienda.service;

import com.linblack.tienda.dto.AuthRequestDTO;
import com.linblack.tienda.dto.AuthResponseDTO;
import com.linblack.tienda.entity.Users;
import com.linblack.tienda.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Lógica de Login
    public AuthResponseDTO login(AuthRequestDTO request) {
        Optional<Users> userOpt = usersRepository.findByUsernameOrEmail(request.getUser(), request.getUser());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Credenciales inválidas");
        }

        Users user = userOpt.get();

        if (!user.getActive() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas o usuario inactivo");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRolId());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setJwt(token);
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        return response;
    }

    // Lógica de Registro (Siempre crea CLIENTE)
    public AuthResponseDTO register(AuthRequestDTO request) {
        if (usersRepository.findByUsername(request.getUser()).isPresent() || 
            usersRepository.findByEmail(request.getUser()).isPresent()) {
            throw new RuntimeException("El usuario o email ya está en uso");
        }

        Users newUser = new Users();
        newUser.setUsername(request.getUser());
        newUser.setEmail(request.getUser() + "@tienda.com"); // Email genérico para el registro rápido
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setActive(true);
        newUser.setRolId(2L); // 2 = CLIENTE

        usersRepository.save(newUser);

        String token = jwtService.generateToken(newUser.getId(), newUser.getUsername(), newUser.getRolId());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setJwt(token);
        response.setUsername(newUser.getUsername());
        response.setEmail(newUser.getEmail());

        return response;
    }
}