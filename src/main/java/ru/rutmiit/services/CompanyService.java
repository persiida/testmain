package ru.rutmiit.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.rutmiit.dto.AddCompanyDto;
import ru.rutmiit.dto.ShowCompanyInfoDto;
import ru.rutmiit.dto.ShowDetailedCompanyInfoDto;

import java.util.List;

public interface CompanyService {

    void addCompany(AddCompanyDto companyDTO);

    List<ShowCompanyInfoDto> allCompanies();

    Page<ShowCompanyInfoDto> allCompaniesPaginated(Pageable pageable);

    List<ShowCompanyInfoDto> searchCompanies(String searchTerm);

    List<ShowCompanyInfoDto> findByTown(String town);

    List<ShowCompanyInfoDto> findByBudgetGreaterThan(Double minBudget);

    ShowDetailedCompanyInfoDto companyDetails(String companyName);

    void removeCompany(String companyName);
}
