package org.example.buraczydlobackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.buraczydlobackend.dto.UserDTO;
import org.example.buraczydlobackend.model.User;
import org.example.buraczydlobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired // <-- upewnij się, że masz to albo użyj konstruktora
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByLogin(userDTO.getLogin())) {
            throw new IllegalArgumentException("Login is already taken");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setSpotifyRefreshToken(userDTO.getSpotifyRefreshToken());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Check if login is being changed and if it's already taken
        if (!user.getLogin().equals(userDTO.getLogin()) && 
            userRepository.existsByLogin(userDTO.getLogin())) {
            throw new IllegalArgumentException("Login is already taken");
        }

        // Check if email is being changed and if it's already in use
        if (!user.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        user.setLogin(userDTO.getLogin());
        user.setEmail(userDTO.getEmail());
        user.setSpotifyRefreshToken(userDTO.getSpotifyRefreshToken());

        // Only update password if it's provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Brak autoryzacji");
        }

        String username = authentication.getName();
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setLogin(user.getLogin());
        userDTO.setEmail(user.getEmail());
        userDTO.setSpotifyRefreshToken(user.getSpotifyRefreshToken());
        // Don't set password in DTO for security reasons
        return userDTO;
    }
}
