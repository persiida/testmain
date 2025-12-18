package ru.rutmiit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rutmiit.models.entities.Role;
import ru.rutmiit.models.enums.UserRoles;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findRoleByName(UserRoles role);
}
