package ru.rutmiit.repositories;

import ru.rutmiit.models.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    
    // Простой поиск по имени
    Optional<Company> findByName(String name);

    // Проверка существования
    boolean existsByName(String name);

    // Поиск по городу
    List<Company> findByTown(String town);

    // Поиск с условием и сортировкой
    List<Company> findByBudgetGreaterThanOrderByBudgetDesc(Double minBudget);

    // Custom Query для полнотекстового поиска
    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Company> searchByNameOrDescription(@Param("searchTerm") String searchTerm);

    // Удаление по имени
    @Modifying
    @Transactional
    void deleteByName(String name);
}

