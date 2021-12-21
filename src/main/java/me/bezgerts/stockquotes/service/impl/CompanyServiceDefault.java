package me.bezgerts.stockquotes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.client.CompanyClient;
import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.entity.Company;
import me.bezgerts.stockquotes.repository.CompanyRepository;
import me.bezgerts.stockquotes.service.CompanyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CompanyServiceDefault implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyClient companyClient;

    @Override
    public List<CompanyDto> getAllCompanies() {
        return companyClient.getAllCompanies();
    }

    @Override
    public void saveCompany(CompanyDto companyDto) {
        Company company = new Company();
        BeanUtils.copyProperties(companyDto, company);
        companyRepository.save(company);
    }
}
