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

        // ПРИНУДИТЕЛЬНО УДАЛЯЕМ ВСЕХ СТАРЫХ ПОЛЬЗОВАТЕЛЕЙ
        userService.getAllUsers().forEach(user -> userService.deleteUser(user.getId()));

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

        // Очищаем всех пользователей
        userService.getAllUsers().forEach(user -> userService.deleteUser(user.getId()));

        // Создаём админа с зашифрованным паролем
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setEmail("admin@mail.ru");
        admin.setAge(35);
        admin.setPassword(passwordEncoder.encode("admin")); // пароль admin

        Set<Role> adminRoles = new LinkedHashSet<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole);
        admin.setRoles(adminRoles);
        userService.saveUser(admin);

        // Создаём юзера с зашифрованным паролем
        User user = new User();
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmail("user@mail.ru");
        user.setAge(30);
        user.setPassword(passwordEncoder.encode("user")); // пароль user

        Set<Role> userRoles = new LinkedHashSet<>();
        userRoles.add(userRole);
        user.setRoles(userRoles);
        userService.saveUser(user);

        System.out.println("=== DataInitializer: users created with encrypted passwords ===");



        // === ПРОВЕРКА ПАРОЛЕЙ ===
        System.out.println("=== ПРОВЕРКА ПАРОЛЕЙ ===");
        User adminCheck = userService.findByEmail("admin@mail.ru");
        if (adminCheck != null) {
            boolean adminMatch = passwordEncoder.matches("admin", adminCheck.getPassword());
            System.out.println("Пароль 'admin' совпадает с БД: " + adminMatch);

            boolean adminMatchWrong = passwordEncoder.matches("admin123", adminCheck.getPassword());
            System.out.println("Пароль 'admin123' совпадает с БД: " + adminMatchWrong);
        }

        User userCheck = userService.findByEmail("user@mail.ru");
        if (userCheck != null) {
            boolean userMatch = passwordEncoder.matches("user", userCheck.getPassword());
            System.out.println("Пароль 'user' совпадает с БД: " + userMatch);
        }
        System.out.println("=== КОНЕЦ ПРОВЕРКИ ===");
    }
}