package org.example.finaccesscore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendPointDTO {
    private String period; // e.g., "2024-01"
    private BigDecimal income;
    private BigDecimal expense;
}