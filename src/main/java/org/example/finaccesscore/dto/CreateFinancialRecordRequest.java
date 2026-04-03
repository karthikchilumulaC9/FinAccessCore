package org.example.finaccesscore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.finaccesscore.model.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.example.finaccesscore.constants.AppConstants.*;

@Data
public class CreateFinancialRecordRequest {
    @NotNull(message = AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = AMOUNT_POSITIVE)
    private BigDecimal amount;

    @NotNull(message = TYPE_REQUIRED)
    private RecordType type;

    @NotBlank(message = CATEGORY_REQUIRED)
    private String category;

    @NotNull(message = DATE_REQUIRED)
    private LocalDate date;

    private String notes;
}