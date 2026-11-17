package com.linblack.tienda.config;

import com.linblack.tienda.entity.Roles;
import com.linblack.tienda.entity.Users;
import com.linblack.tienda.repository.RolesRepository;
import com.linblack.tienda.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public void run(String... args) throws Exception {
        //  Crear los roles
        if (rolesRepository.count() == 0) {
            Roles adminRole = new Roles(); adminRole.setName("ADMIN");
            Roles clienteRole = new Roles(); clienteRole.setName("CLIENTE");
            Roles vendedorRole = new Roles(); vendedorRole.setName("VENDEDOR");
            
            rolesRepository.save(adminRole);
            rolesRepository.save(clienteRole);
            rolesRepository.save(vendedorRole);
            System.out.println("✅ Roles creados con éxito.");
        }

        //  Crear el usuario ADMIN inicial si no existe
        if (usersRepository.findByUsername("admin").isEmpty()) {
            Users admin = new Users();
            admin.setUsername("admin");
            admin.setEmail("admin@tienda.com");
            admin.setActive(true);
            admin.setRolId(1L); 

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            admin.setPassword(encoder.encode("admin123")); // Contraseña: admin123

            usersRepository.save(admin);
            System.out.println("✅ Usuario ADMIN creado con contraseña 'admin123'.");
        }
    }
}
