package me.bezgerts.stockquotes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.client.CompanyClient;
import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.entity.Company;
import me.bezgerts.stockquotes.mapper.CompanyMapper;
import me.bezgerts.stockquotes.repository.CompanyRepository;
import me.bezgerts.stockquotes.service.CompanyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CompanyServiceDefault implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CompanyClient companyClient;

    @Override
    public List<CompanyDto> getAllCompanies() {
        return companyClient.getAllCompanies();
    }

    @Override
    public List<CompanyDto> updateCompanyList(List<CompanyDto> companyDtoList) {
        companyDtoList.stream()
                .filter(CompanyDto::getIsEnabled)
                .forEach(this::updateCompany);
        return companyRepository.findAll()
                .stream()
                .filter(Company::getIsEnabled)
                .map(companyMapper::companyDtoFromCompany)
                .collect(Collectors.toList());
    }

    private void updateCompany(CompanyDto companyDto) {
        Optional<Company> companyOptional = companyRepository.findById(companyDto.getSymbol());
        if (companyOptional.isPresent()) {
            Company companyFromDb = companyOptional.get();
            Company updatedCompany = companyMapper.companyFromCompanyDto(companyDto);
            if (!companyFromDb.equals(updatedCompany)) {
                BeanUtils.copyProperties(updatedCompany, companyFromDb);
                companyRepository.save(companyFromDb);
            }
        } else {
            Company company = companyMapper.companyFromCompanyDto(companyDto);
            companyRepository.save(company);
        }
    }
}
