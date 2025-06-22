package com.oclock.api.controller;

import com.oclock.api.dto.UserCreateUpdateDTO;
import com.oclock.api.model.LoginRequest;
import com.oclock.api.model.User;
import com.oclock.api.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController // Marca esta classe como um controlador REST
@RequestMapping("/api/users") // Define a URL base para todos os endpoints deste controller
public class UserController {

    private final UserService userService;

    @Autowired // Injeção de dependência do UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateUpdateDTO userDTO) {
        try {
            User newUser = userService.createUser(userDTO);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED); // Retorna 201 Created
        } catch (IllegalArgumentException e) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK); // Retorna 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK)) // Se encontrar, retorna 200 OK
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com ID: " + id)); // Se não encontrar, retorna 404 Not Found
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @Valid @RequestBody UserCreateUpdateDTO userDTO) {
        try {
            User updatedUser = userService.updateUser(id, userDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK); // Retorna 200 OK
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()); // Retorna 404 Not Found
        } catch (IllegalArgumentException e) {
            // Se o email ou CPF atualizado já existirem
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content (sucesso sem corpo)
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()); // Retorna 404 Not Found
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {

        Optional<User> authenticatedUser = userService.authenticateUser(
                loginRequest.getEmail(), loginRequest.getPassword()
        );

        return authenticatedUser
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos."));
    }
}