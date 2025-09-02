package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

public interface UserService {
    void saveOrUpdateUser(Long id, String firstName, String lastName, String email,
                          int age, String password, List<String> roles);
    void deleteUser(Long id);
    void saveUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User findByEmail(String email);
}