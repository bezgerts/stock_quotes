package me.bezgerts.stockquotes.repository;

import me.bezgerts.stockquotes.entity.QuoteInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteInfoRepository extends CrudRepository<QuoteInfo, String> {
}
