package ru.rutmiit.dto;

import ru.rutmiit.models.enums.EducationLevel;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class AddEmployeeDto {

    private String firstName;

    private String lastName;
    private EducationLevel educationLevel;
    private String companyName;
    private String jobTitle;
    private LocalDate birthDate;
    private Double salary;

    @NotEmpty(message = "Имя не должно быть пустым!")
    @Size(min = 2, message = "Имя должно содержать не менее 2 символов!")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    @NotEmpty(message = "Фамилия не должна быть пустой!")
    @Size(min = 2, message = "Фамилия должна содержать не менее 2 символов!")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    @NotNull(message = "Выберите уровень образования!")
    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }
    @NotEmpty(message = "Выберите компанию!")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    @NotEmpty(message = "Должность не должна быть пустой!")
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    @NotNull(message = "Дата рождения не должна быть пустой!")
    @PastOrPresent(message = "Дата рождения не может быть в будущем!")
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    @NotNull(message = "Зарплата не должна быть пустой!")
    @DecimalMin(value = "0.01", message = "Зарплата должна быть положительным числом!")
    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }
}
