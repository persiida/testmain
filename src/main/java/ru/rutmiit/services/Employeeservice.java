package ru.rutmiit.services;

import ru.rutmiit.dto.AddEmployeeDto;
import ru.rutmiit.dto.ShowDetailedEmployeeInfoDto;
import ru.rutmiit.dto.ShowEmployeeInfoDto;

import java.util.List;

public interface EmployeeService {
    void addEmployee(AddEmployeeDto employeeDTO);

    List<ShowEmployeeInfoDto> allEmployees();

    ShowDetailedEmployeeInfoDto employeeInfo(String employeeFullName);

    void fireEmployee(String employeeFullName);
    
    void transferEmployee(String employeeFullName, String newCompanyName);
}
