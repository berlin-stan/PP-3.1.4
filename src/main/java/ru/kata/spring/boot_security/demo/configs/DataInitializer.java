package ru.kata.spring.boot_security.demo.configs;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class DataInitializer {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public DataInitializer(UserService userService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }

        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }

        User existingAdmin = userService.findByEmail("admin@mail.com");
        if (existingAdmin != null) {
            userService.deleteUser(existingAdmin.getId());
        }

        User existingUser = userService.findByEmail("user@mail.com");
        if (existingUser != null) {
            userService.deleteUser(existingUser.getId());
        }

        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setEmail("admin@mail.com");
        admin.setAge(35);
        admin.setPassword(passwordEncoder.encode("admin"));

        Set<Role> adminRoles = new LinkedHashSet<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);
        admin.setRoles(adminRoles);
        userService.saveUser(admin);

        User user = new User();
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmail("user@mail.com");
        user.setAge(30);
        user.setPassword(passwordEncoder.encode("user"));

        Set<Role> userRoles = new LinkedHashSet<>();
        userRoles.add(userRole);
        user.setRoles(userRoles);
        userService.saveUser(user);
    }
}