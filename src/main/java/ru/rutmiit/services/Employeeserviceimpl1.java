package ru.rutmiit.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.AddEmployeeDto;
import ru.rutmiit.dto.ShowDetailedEmployeeInfoDto;
import ru.rutmiit.dto.ShowEmployeeInfoDto;
import ru.rutmiit.models.entities.Company;
import ru.rutmiit.models.entities.Employee;
import ru.rutmiit.models.exceptions.CompanyNotFoundException;
import ru.rutmiit.models.exceptions.EmployeeNotFoundException;
import ru.rutmiit.repositories.CompanyRepository;
import ru.rutmiit.repositories.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapper mapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, CompanyRepository companyRepository, ModelMapper mapper) {
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
        this.mapper = mapper;
        log.info("EmployeeServiceImpl инициализирован");
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "employees", allEntries = true)
    public void addEmployee(AddEmployeeDto employeeDTO) {
        log.debug("Добавление нового сотрудника: {} {}", employeeDTO.getFirstName(), employeeDTO.getLastName());

        Employee employee = mapper.map(employeeDTO, Employee.class);
        employee.setCompany(companyRepository.findByName(employeeDTO.getCompanyName()).orElse(null));

        employeeRepository.saveAndFlush(employee);
        log.info("Сотрудник успешно добавлен: {} {}", employeeDTO.getFirstName(), employeeDTO.getLastName());
    }

    @Override
    @Cacheable(value = "employees", key = "'all'")
    public List<ShowEmployeeInfoDto> allEmployees() {
        log.debug("Получение списка всех сотрудников");
        List<ShowEmployeeInfoDto> employees = employeeRepository.findAll().stream()
                .map(employee -> mapper.map(employee, ShowEmployeeInfoDto.class))
                .collect(Collectors.toList());
        log.debug("Найдено сотрудников: {}", employees.size());
        return employees;
    }

    @Override
    public ShowDetailedEmployeeInfoDto employeeInfo(String employeeFullName) {
        log.debug("Получение информации о сотруднике: {}", employeeFullName);
        Employee employee = employeeRepository.findEmployeeByFullName(employeeFullName);

        if (employee == null) {
            log.warn("Сотрудник не найден: {}", employeeFullName);
            throw new EmployeeNotFoundException("Сотрудник с именем '" + employeeFullName + "' не найден");
        }

        ShowDetailedEmployeeInfoDto dto = mapper.map(employee, ShowDetailedEmployeeInfoDto.class);
        if (employee.getCompany() != null) {
            dto.setCompanyName(employee.getCompany().getName());
        }
        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "employees", allEntries = true)
    public void fireEmployee(String employeeFullName) {
        log.debug("Увольнение сотрудника: {}", employeeFullName);

        Employee employee = employeeRepository.findEmployeeByFullName(employeeFullName);
        if (employee == null) {
            log.warn("Попытка уволить несуществующего сотрудника: {}", employeeFullName);
            throw new EmployeeNotFoundException("Сотрудник с именем '" + employeeFullName + "' не найден");
        }

        employeeRepository.deleteEmployeeByFullName(employeeFullName);
        log.info("Сотрудник уволен: {}", employeeFullName);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "employees", allEntries = true)
    public void transferEmployee(String employeeFullName, String newCompanyName) {
        log.debug("Перевод сотрудника {} в компанию {}", employeeFullName, newCompanyName);

        Employee employee = employeeRepository.findEmployeeByFullName(employeeFullName);
        if (employee == null) {
            log.warn("Сотрудник не найден: {}", employeeFullName);
            throw new EmployeeNotFoundException("Сотрудник с именем '" + employeeFullName + "' не найден");
        }

        Company newCompany = companyRepository.findByName(newCompanyName)
                .orElseThrow(() -> {
                    log.warn("Компания не найдена: {}", newCompanyName);
                    return new CompanyNotFoundException("Компания с именем '" + newCompanyName + "' не найдена");
                });

        String oldCompanyName = employee.getCompany() != null ? employee.getCompany().getName() : "Нет компании";
        employee.setCompany(newCompany);
        employeeRepository.save(employee);

        log.info("Сотрудник {} переведен из '{}' в '{}'", employeeFullName, oldCompanyName, newCompanyName);
    }
}
