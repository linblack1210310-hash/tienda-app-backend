   package com.linblack.tienda.controller;

import com.linblack.tienda.dto.HttpGlobalResponseDTO;
import com.linblack.tienda.entity.Users;
import com.linblack.tienda.repository.UsersRepository;
import com.linblack.tienda.security.RequiresRole;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersRepository usersRepository;

    @RequiresRole({1, 2, 3})
    @GetMapping("/me")
    public ResponseEntity<HttpGlobalResponseDTO<Users>> getMe(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Users user = usersRepository.findById(userId).orElseThrow();
        user.setPassword(null);
        return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Perfil obtenido", user));
    }

    // Listar todos (Solo ADMIN)
    @RequiresRole({1})
    @GetMapping
    public ResponseEntity<HttpGlobalResponseDTO<List<Users>>> getAll() {
        List<Users> users = usersRepository.findAll();
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Usuarios listados", users));
    }

    // Crear usuario (Solo ADMIN) - Este es el que faltaba
    @RequiresRole({1})
    @PostMapping
    public ResponseEntity<HttpGlobalResponseDTO<Users>> create(@RequestBody Users user) {
        if (usersRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new HttpGlobalResponseDTO<>("El username ya existe", null));
        }
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setActive(true);
        
        Users saved = usersRepository.save(user);
        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(new HttpGlobalResponseDTO<>("Usuario creado", saved));
    }
}