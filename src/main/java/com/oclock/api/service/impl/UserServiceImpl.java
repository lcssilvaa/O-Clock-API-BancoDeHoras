package com.oclock.api.service.impl;

import com.oclock.api.dto.UserCreateUpdateDTO;
import com.oclock.api.model.User;
import com.oclock.api.repository.UserRepository;
import com.oclock.api.service.UserService;
import com.oclock.api.util.Sha256Hasher;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserCreateUpdateDTO userDTO) {

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este email.");
        }
        if (userRepository.findByCpf(userDTO.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este CPF.");
        }

        User user = new User();
        user.setNomeCompleto(userDTO.getNomeCompleto());
        user.setEmail(userDTO.getEmail());

        user.setPasswordHash(Sha256Hasher.hash(userDTO.getPassword()));
        user.setCpf(userDTO.getCpf());
        user.setJornadaDiariaHoras(userDTO.getJornadaDiariaHoras());
        user.setPermissao(userDTO.getPermissao().toLowerCase());
        user.setActive(userDTO.getActive());
        user.setValorHora(userDTO.getValorHora());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User updateUser(Integer userId, UserCreateUpdateDTO userDTO) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + userId + " não encontrado para atualização."));

        if (!existingUser.getEmail().equals(userDTO.getEmail())) {
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("O email '" + userDTO.getEmail() + "' já está em uso por outro usuário.");
            }
        }
        if (!existingUser.getCpf().equals(userDTO.getCpf())) {
            if (userRepository.findByCpf(userDTO.getCpf()).isPresent()) {
                throw new IllegalArgumentException("O CPF '" + userDTO.getCpf() + "' já está em uso por outro usuário.");
            }
        }

        existingUser.setNomeCompleto(userDTO.getNomeCompleto());
        existingUser.setEmail(userDTO.getEmail());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPasswordHash(Sha256Hasher.hash(userDTO.getPassword()));
        }
        existingUser.setCpf(userDTO.getCpf());
        existingUser.setJornadaDiariaHoras(userDTO.getJornadaDiariaHoras());
        existingUser.setPermissao(userDTO.getPermissao().toLowerCase());
        existingUser.setActive(userDTO.getActive());
        existingUser.setValorHora(userDTO.getValorHora());

        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Usuário com ID " + userId + " não encontrado para exclusão.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (Sha256Hasher.verify(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}