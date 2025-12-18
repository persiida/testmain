package ru.rutmiit.services;

import ru.rutmiit.dto.UserRegistrationDto;
import ru.rutmiit.models.entities.User;

public interface AuthService {
    void register(UserRegistrationDto registrationDTO);

    User getUser(String username);
}
