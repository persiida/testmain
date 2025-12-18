package ru.rutmiit.web;

import lombok.extern.slf4j.Slf4j;
import ru.rutmiit.dto.AddEmployeeDto;
import ru.rutmiit.dto.TransferEmployeeDto;
import ru.rutmiit.services.CompanyService;
import ru.rutmiit.services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final CompanyService companyService;

    public EmployeeController(EmployeeService employeeService, CompanyService companyService) {
        this.employeeService = employeeService;
        this.companyService = companyService;
        log.info("EmployeeController инициализирован");
    }

    @GetMapping("/add")
    public String addEmployee(Model model) {
        log.debug("Отображение формы добавления сотрудника");
        model.addAttribute("availableCompanies", companyService.allCompanies());

        return "employee-add";
    }

    @ModelAttribute("employeeModel")
    public AddEmployeeDto initEmployee() {
        return new AddEmployeeDto();
    }

    @PostMapping("/add")
    public String addEmployee(@Valid AddEmployeeDto employeeModel, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.debug("Обработка добавления сотрудника: {} {}", employeeModel.getFirstName(), employeeModel.getLastName());
        
        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при добавлении сотрудника: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("employeeModel", employeeModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.employeeModel",
                    bindingResult);
            return "redirect:/employees/add";
        }
        employeeService.addEmployee(employeeModel);
        log.info("Сотрудник успешно добавлен через контроллер");

        return "redirect:/";
    }

    @GetMapping("/all")
    public String showAllEmployees(Model model) {
        log.debug("Отображение списка всех сотрудников");
        model.addAttribute("allEmployees", employeeService.allEmployees());

        return "employee-all";
    }

    @GetMapping("/employee-details/{employee-full-name}")
    public String showEmployeeDetails(@PathVariable("employee-full-name") String employeeFullName, Model model) {
        log.debug("Отображение деталей сотрудника: {}", employeeFullName);
        model.addAttribute("employeeDetails", employeeService.employeeInfo(employeeFullName));

        return "employee-details";
    }

    @DeleteMapping("/employee-delete/{employee-full-name}")
    public String fireEmployee(@PathVariable("employee-full-name") String employeeFullName) {
        log.debug("Увольнение сотрудника через контроллер: {}", employeeFullName);
        employeeService.fireEmployee(employeeFullName);
        log.info("Сотрудник уволен через контроллер: {}", employeeFullName);

        return "redirect:/employees/all";
    }

    @GetMapping("/employee-transfer/{employee-full-name}")
    public String showTransferForm(@PathVariable("employee-full-name") String employeeFullName, Model model) {
        log.debug("Отображение формы перевода сотрудника: {}", employeeFullName);
        
        model.addAttribute("employeeDetails", employeeService.employeeInfo(employeeFullName));
        model.addAttribute("availableCompanies", companyService.allCompanies());
        model.addAttribute("transferModel", new TransferEmployeeDto());
        model.addAttribute("employeeFullName", employeeFullName);

        return "employee-transfer";
    }

    @PostMapping("/employee-transfer/{employee-full-name}")
    public RedirectView transferEmployee(
            @PathVariable("employee-full-name") String employeeFullName,
            @Valid @ModelAttribute("transferModel") TransferEmployeeDto transferModel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        log.debug("Обработка перевода сотрудника: {} в компанию {}", 
                  employeeFullName, transferModel.getNewCompanyName());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при переводе сотрудника: {}", bindingResult.getAllErrors());
            model.addAttribute("employeeDetails", employeeService.employeeInfo(employeeFullName));
            model.addAttribute("availableCompanies", companyService.allCompanies());
            model.addAttribute("employeeFullName", employeeFullName);
            return new RedirectView("/employees/employee-transfer/" + employeeFullName, true);
        }

        employeeService.transferEmployee(employeeFullName, transferModel.getNewCompanyName());
        redirectAttributes.addFlashAttribute("successMessage", 
            "Сотрудник " + employeeFullName + " успешно переведен в компанию " + transferModel.getNewCompanyName());
        
        log.info("Сотрудник {} успешно переведен через контроллер", employeeFullName);

        // RedirectView автоматически энкодит URL
        RedirectView redirectView = new RedirectView("/employees/employee-details/" + employeeFullName, true);
        return redirectView;
    }
}
