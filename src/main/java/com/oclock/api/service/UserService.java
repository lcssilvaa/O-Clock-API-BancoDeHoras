package com.oclock.api.service;

import com.oclock.api.dto.UserCreateUpdateDTO;
import com.oclock.api.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(UserCreateUpdateDTO userDTO);

    List<User> getAllUsers();

    Optional<User> getUserById(Integer userId);

    User updateUser(Integer userId, UserCreateUpdateDTO userDTO);

    void deleteUser(Integer userId);

    // Método para autenticação. Retorna o usuário se as credenciais forem válidas.
    Optional<User> authenticateUser(String email, String password);
}