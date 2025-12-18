package ru.rutmiit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.rutmiit.models.entities.Role;
import ru.rutmiit.models.entities.User;
import ru.rutmiit.models.enums.UserRoles;
import ru.rutmiit.repositories.UserRepository;
import ru.rutmiit.repositories.UserRoleRepository;

import java.util.List;

/**
 * Инициализация начальных данных при запуске приложения.
 */
@Slf4j
@Component
public class Init implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultPassword;

    public Init(UserRepository userRepository, 
                UserRoleRepository userRoleRepository, 
                PasswordEncoder passwordEncoder, 
                @Value("${app.default.password}") String defaultPassword) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultPassword = defaultPassword;
        log.info("Init компонент инициализирован");
    }

    @Override
    public void run(String... args) {
        log.info("Запуск инициализации начальных данных");
        initRoles();
        initUsers();
        log.info("Инициализация начальных данных завершена");
    }

    private void initRoles() {
        if (userRoleRepository.count() == 0) {
            log.info("Создание базовых ролей...");
            userRoleRepository.saveAll(List.of(
                new Role(UserRoles.ADMIN),
                new Role(UserRoles.MODERATOR),
                new Role(UserRoles.USER)
            ));
            log.info("Роли созданы: ADMIN, MODERATOR, USER");
        } else {
            log.debug("Роли уже существуют, пропуск инициализации");
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            log.info("Создание пользователей по умолчанию...");
            initAdmin();
            initModerator();
            initNormalUser();
            log.info("Пользователи по умолчанию созданы");
        } else {
            log.debug("Пользователи уже существуют, пропуск инициализации");
        }
    }

    private void initAdmin() {
        var adminRole = userRoleRepository
                .findRoleByName(UserRoles.ADMIN)
                .orElseThrow();

        var adminUser = new User(
            "admin", 
            passwordEncoder.encode(defaultPassword), 
            "admin@example.com", 
            "Admin Adminovich", 
            30
        );
        adminUser.setRoles(List.of(adminRole));
        userRepository.save(adminUser);
        log.info("Создан администратор: admin");
    }

    private void initModerator() {
        var moderatorRole = userRoleRepository
                .findRoleByName(UserRoles.MODERATOR)
                .orElseThrow();

        var moderatorUser = new User(
            "moderator", 
            passwordEncoder.encode(defaultPassword), 
            "moderator@example.com", 
            "Moder Moderovich", 
            24
        );
        moderatorUser.setRoles(List.of(moderatorRole));
        userRepository.save(moderatorUser);
        log.info("Создан модератор: moderator");
    }

    private void initNormalUser() {
        var userRole = userRoleRepository
                .findRoleByName(UserRoles.USER)
                .orElseThrow();

        var normalUser = new User(
            "user", 
            passwordEncoder.encode(defaultPassword), 
            "user@example.com", 
            "User Userovich", 
            22
        );
        normalUser.setRoles(List.of(userRole));
        userRepository.save(normalUser);
        log.info("Создан обычный пользователь: user");
    }
}
