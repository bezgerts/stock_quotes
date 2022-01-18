package me.bezgerts.stockquotes.service;

import me.bezgerts.stockquotes.dto.CompanyDto;

import java.util.List;

public interface CompanyService {
    CompanyDto findCompanyBySymbol(String symbol);

    List<CompanyDto> getAllCompaniesFromIEX();

    List<CompanyDto> updateCompanyList(List<CompanyDto> companyDtoList);
}
