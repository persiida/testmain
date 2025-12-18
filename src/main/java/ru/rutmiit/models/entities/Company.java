package ru.rutmiit.models.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "companies")
public class Company extends BaseEntity {
    
    @Column(columnDefinition = "DECIMAL(19,2)", nullable = false)
    private Double budget;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String town;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Employee> employees = new HashSet<>();

    public Company() {
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }
}
