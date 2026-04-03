package org.example.finaccesscore.dto;

import lombok.Data;
import org.example.finaccesscore.model.RecordType;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinancialRecordDTO {
    private Long id;
    private BigDecimal amount;
    private RecordType type;
    private String category;
    private LocalDate date;
    private String notes;
    private Long userId;
    private String username;
}