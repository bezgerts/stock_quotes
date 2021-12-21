package me.bezgerts.stockquotes.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @Id
    private String symbol;
    private String name;
    private LocalDate date;
    private String type;
    private String iexId;
    private String region;
    private String currency;
    private Boolean isEnabled;
    private String figi;
    private String cik;
}
