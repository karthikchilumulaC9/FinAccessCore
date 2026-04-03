package org.example.finaccesscore.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardSummaryDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> categoryWiseTotals;
    private List<FinancialRecordDTO> recentActivity;
    private List<TrendPointDTO> monthlyTrends;
}