package me.bezgerts.stockquotes.repository;

import me.bezgerts.stockquotes.entity.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends CrudRepository<Company, String> {
    List<Company> findAll();
}
