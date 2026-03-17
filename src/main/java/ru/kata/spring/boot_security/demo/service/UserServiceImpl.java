package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        // Шифруем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Загружаем существующие роли из БД по их именам
        Set<Role> managedRoles = new LinkedHashSet<>();
        for (Role role : user.getRoles()) {
            // Ищем роль в БД по имени
            Role existingRole = roleRepository.findByName(role.getName());
            if (existingRole != null) {
                managedRoles.add(existingRole);  // добавляем роль из БД (с ID)
            } else {
                // Если роли нет в БД (а такое вряд ли) - создаем
                Role newRole = new Role();
                newRole.setName(role.getName());
                roleRepository.save(newRole);
                managedRoles.add(newRole);
            }
        }
        user.setRoles(managedRoles);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existingUser = getUserById(user.getId());
        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setAge(user.getAge());

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            userRepository.save(existingUser);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}