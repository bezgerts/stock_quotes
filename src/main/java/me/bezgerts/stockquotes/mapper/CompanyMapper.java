package me.bezgerts.stockquotes.mapper;

import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.entity.Company;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public Company companyFromCompanyDto(CompanyDto companyDto) {
        Company result = new Company();
        BeanUtils.copyProperties(companyDto, result);
        return result;
    }

    public CompanyDto companyDtoFromCompany(Company company) {
        CompanyDto result = new CompanyDto();
        BeanUtils.copyProperties(company, result);
        return result;
    }
}
