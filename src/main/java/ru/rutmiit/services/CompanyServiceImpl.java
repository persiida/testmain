package ru.rutmiit.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.AddCompanyDto;
import ru.rutmiit.dto.ShowCompanyInfoDto;
import ru.rutmiit.dto.ShowDetailedCompanyInfoDto;
import ru.rutmiit.models.entities.Company;
import ru.rutmiit.models.exceptions.CompanyNotFoundException;
import ru.rutmiit.repositories.CompanyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final ModelMapper mapper;

    public CompanyServiceImpl(CompanyRepository companyRepository, ModelMapper mapper) {
        this.companyRepository = companyRepository;
        this.mapper = mapper;
        log.info("CompanyServiceImpl инициализирован");
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "companies", allEntries = true)
    public void addCompany(AddCompanyDto companyDTO) {
        log.debug("Добавление новой компании: {}", companyDTO.getName());
        Company company = mapper.map(companyDTO, Company.class);
        companyRepository.save(company);
        log.info("Компания успешно добавлена: {} в городе {}", company.getName(), company.getTown());
    }

    @Override
    @Cacheable(value = "companies", key = "'all'")
    public List<ShowCompanyInfoDto> allCompanies() {
        log.debug("Получение списка всех компаний");
        List<ShowCompanyInfoDto> companies = companyRepository.findAll().stream()
                .map(company -> mapper.map(company, ShowCompanyInfoDto.class))
                .collect(Collectors.toList());
        log.info("Найдено компаний: {}", companies.size());
        return companies;
    }

    @Override
    public Page<ShowCompanyInfoDto> allCompaniesPaginated(Pageable pageable) {
        log.debug("Получение компаний с пагинацией: страница {}, размер {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return companyRepository.findAll(pageable)
                .map(company -> mapper.map(company, ShowCompanyInfoDto.class));
    }

    @Override
    public List<ShowCompanyInfoDto> searchCompanies(String searchTerm) {
        log.debug("Поиск компаний по запросу: {}", searchTerm);
        List<ShowCompanyInfoDto> results = companyRepository.searchByNameOrDescription(searchTerm).stream()
                .map(company -> mapper.map(company, ShowCompanyInfoDto.class))
                .collect(Collectors.toList());
        log.info("По запросу '{}' найдено компаний: {}", searchTerm, results.size());
        return results;
    }

    @Override
    public List<ShowCompanyInfoDto> findByTown(String town) {
        log.debug("Поиск компаний в городе: {}", town);
        return companyRepository.findByTown(town).stream()
                .map(company -> mapper.map(company, ShowCompanyInfoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowCompanyInfoDto> findByBudgetGreaterThan(Double minBudget) {
        log.debug("Поиск компаний с бюджетом больше: {}", minBudget);
        return companyRepository.findByBudgetGreaterThanOrderByBudgetDesc(minBudget).stream()
                .map(company -> mapper.map(company, ShowCompanyInfoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "company", key = "#companyName", unless = "#result == null")
    public ShowDetailedCompanyInfoDto companyDetails(String companyName) {
        log.debug("Получение деталей компании: {}", companyName);
        Company company = companyRepository.findByName(companyName)
                .orElseThrow(() -> {
                    log.warn("Компания не найдена: {}", companyName);
                    return new CompanyNotFoundException("Компания с именем '" + companyName + "' не найдена");
                });
        return mapper.map(company, ShowDetailedCompanyInfoDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"companies", "company", "employees"}, allEntries = true)
    public void removeCompany(String companyName) {
        log.debug("Удаление компании: {}", companyName);
        if (!companyRepository.existsByName(companyName)) {
            log.warn("Попытка удалить несуществующую компанию: {}", companyName);
            throw new CompanyNotFoundException("Компания с именем '" + companyName + "' не найдена");
        }
        companyRepository.deleteByName(companyName);
        log.info("Компания успешно удалена: {}", companyName);
    }
}
