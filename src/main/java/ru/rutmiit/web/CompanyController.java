package ru.rutmiit.web;

import lombok.extern.slf4j.Slf4j;
import ru.rutmiit.dto.AddCompanyDto;
import ru.rutmiit.services.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rutmiit.dto.ShowCompanyInfoDto;

@Slf4j
@Controller
@RequestMapping("/companies")
public class CompanyController {
    
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
        log.info("CompanyController инициализирован");
    }

    @GetMapping("/add")
    public String addCompany() {
        log.debug("Отображение формы добавления компании");
        return "company-add";
    }

    @ModelAttribute("companyModel")
    public AddCompanyDto initCompany() {
        return new AddCompanyDto();
    }

    @PostMapping("/add")
    public String addCompany(@Valid AddCompanyDto companyModel, 
                           BindingResult bindingResult, 
                           RedirectAttributes redirectAttributes) {
        log.debug("Обработка POST запроса на добавление компании");
        
        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при добавлении компании: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("companyModel", companyModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.companyModel",
                    bindingResult);
            return "redirect:/companies/add";
        }
        
        companyService.addCompany(companyModel);
        redirectAttributes.addFlashAttribute("successMessage", 
            "Компания '" + companyModel.getName() + "' успешно добавлена!");
        
        return "redirect:/companies/all";
    }

    @GetMapping("/all")
    public String showAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String search,
            Model model) {
        
        log.debug("Отображение списка компаний: страница={}, размер={}, сортировка={}, поиск={}", 
                  page, size, sortBy, search);

        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("companyInfos", companyService.searchCompanies(search));
            model.addAttribute("search", search);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
            Page<ShowCompanyInfoDto> companyPage = companyService.allCompaniesPaginated(pageable);
            
            model.addAttribute("companyInfos", companyPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", companyPage.getTotalPages());
            model.addAttribute("totalItems", companyPage.getTotalElements());
        }

        return "company-all";
    }

    @GetMapping("/company-details/{company-name}")
    public String companyDetails(@PathVariable("company-name") String companyName, Model model) {
        log.debug("Запрос деталей компании: {}", companyName);
        model.addAttribute("companyDetails", companyService.companyDetails(companyName));
        return "company-details";
    }

    @GetMapping("/company-delete/{company-name}")
    public String deleteCompany(@PathVariable("company-name") String companyName,
                              RedirectAttributes redirectAttributes) {
        log.debug("Запрос на удаление компании: {}", companyName);
        companyService.removeCompany(companyName);
        redirectAttributes.addFlashAttribute("successMessage", 
            "Компания '" + companyName + "' успешно удалена!");
        return "redirect:/companies/all";
    }
}
