package ru.rutmiit.dto;

import ru.rutmiit.utils.validation.UniqueCompanyName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class AddCompanyDto {

    @UniqueCompanyName
    private String name;
    private String town;
    private String description;
    private Double budget;

    @NotEmpty(message = "Название компании не должно быть пустым!")
    @Size(min = 2, max = 10, message = "Название компании должно быть от 2 до 10 символов!")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty(message = "Город не должен быть пустым!")
    @Size(min = 2, max = 10, message = "Название города должно быть от 2 до 10 символов!")
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @NotEmpty(message = "Описание не должно быть пустым!")
    @Size(min = 10, message = "Описание должно содержать минимум 10 символов!")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Min(value = 1, message = "Бюджет должен быть положительным числом!")
    @NotNull(message = "Бюджет не должен быть пустым!")
    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }
}
