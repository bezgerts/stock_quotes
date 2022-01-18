package me.bezgerts.stockquotes.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.client.CompanyClient;
import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.entity.Company;
import me.bezgerts.stockquotes.mapper.CompanyMapper;
import me.bezgerts.stockquotes.repository.CompanyRepository;
import me.bezgerts.stockquotes.service.CompanyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CompanyServiceDefault implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CompanyClient companyClient;
    private final Map<String, CompanyDto> companyDtoCache;

    public CompanyServiceDefault(CompanyRepository companyRepository, CompanyMapper companyMapper, CompanyClient companyClient) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.companyClient = companyClient;
        this.companyDtoCache = new ConcurrentHashMap<>();
    }

    @Override
    public CompanyDto findCompanyBySymbol(String symbol) {
        if (companyDtoCache.containsKey(symbol)) {
            return companyDtoCache.get(symbol);
        } else {
            CompanyDto companyDto = companyMapper.companyDtoFromCompany(companyRepository.findById(symbol).orElseThrow());
            companyDtoCache.put(symbol, companyDto);
            return companyDto;
        }
    }

    @Override
    public List<CompanyDto> getAllCompaniesFromIEX() {
        return companyClient.getAllCompanies();
    }

    @Override
    public List<CompanyDto> updateCompanyList(List<CompanyDto> companyDtoList) {
        List<Company> companyList = companyDtoList.stream()
                .filter(CompanyDto::getIsEnabled)
                .map(this::getEntityForSave)
                .collect(Collectors.toList());
        companyRepository.saveAll(companyList);
        return companyList.stream()
                .map(companyMapper::companyDtoFromCompany)
                .collect(Collectors.toList());
    }

    private Company getEntityForSave(CompanyDto companyDto) {
        Optional<Company> companyOptional = companyRepository.findById(companyDto.getSymbol());
        if (companyOptional.isPresent()) {
            Company companyFromDb = companyOptional.get();
            Company updatedCompany = companyMapper.companyFromCompanyDto(companyDto);
            if (!companyFromDb.equals(updatedCompany)) {
                BeanUtils.copyProperties(updatedCompany, companyFromDb);
            }
            return companyFromDb;
        } else {
            return companyMapper.companyFromCompanyDto(companyDto);
        }
    }
}
