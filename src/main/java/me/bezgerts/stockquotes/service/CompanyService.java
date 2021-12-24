package me.bezgerts.stockquotes.service;

import me.bezgerts.stockquotes.dto.CompanyDto;

import java.util.List;

public interface CompanyService {

    List<CompanyDto> getAllCompanies();
    List<CompanyDto> updateCompanyList(List<CompanyDto> companyDtoList);
}
