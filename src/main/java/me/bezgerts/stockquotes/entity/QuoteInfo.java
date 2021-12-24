package me.bezgerts.stockquotes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "quote_info")
@NoArgsConstructor
@AllArgsConstructor
public class QuoteInfo {

    @Id
    private String symbol;
    private String companyName;
    private Long volume;
    private BigDecimal diffPrice;
    private BigDecimal latestPrice;
}
