package me.bezgerts.stockquotes.repository;

import me.bezgerts.stockquotes.entity.QuoteInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteInfoRepository extends CrudRepository<QuoteInfo, String> {
    List<QuoteInfo> findAll();
}
