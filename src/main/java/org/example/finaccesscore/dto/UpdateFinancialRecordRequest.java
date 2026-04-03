package org.example.finaccesscore.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import org.example.finaccesscore.model.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.example.finaccesscore.constants.AppConstants.AMOUNT_POSITIVE;

@Data
public class UpdateFinancialRecordRequest {
    @DecimalMin(value = "0.01", message = AMOUNT_POSITIVE)
    private BigDecimal amount;
    
    private RecordType type;
    private String category;
    private LocalDate date;
    private String notes;
}