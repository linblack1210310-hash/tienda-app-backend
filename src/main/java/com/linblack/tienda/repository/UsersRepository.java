package com.linblack.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linblack.tienda.entity.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsernameOrEmail(String username, String email);
}