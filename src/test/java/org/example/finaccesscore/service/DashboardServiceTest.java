package org.example.finaccesscore.service;

import org.example.finaccesscore.dto.DashboardSummaryDTO;
import org.example.finaccesscore.model.FinancialRecord;
import org.example.finaccesscore.model.RecordType;
import org.example.finaccesscore.repository.FinancialRecordRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @MockitoBean
    private FinancialRecordRepository financialRecordRepository;

    @Test
    public void testGetDashboardSummary() {
        FinancialRecord income = new FinancialRecord();
        income.setAmount(BigDecimal.valueOf(1000));
        income.setType(RecordType.INCOME);
        income.setCategory("Salary");
        income.setDate(LocalDate.now());
        income.setDeleted(false);

        FinancialRecord expense = new FinancialRecord();
        expense.setAmount(BigDecimal.valueOf(400));
        expense.setType(RecordType.EXPENSE);
        expense.setCategory("Rent");
        expense.setDate(LocalDate.now());
        expense.setDeleted(false);

        Mockito.when(financialRecordRepository.findByDeletedFalse())
                .thenReturn(Arrays.asList(income, expense));

        DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
        
        assertNotNull(summary);
        assertEquals(BigDecimal.valueOf(1000), summary.getTotalIncome());
        assertEquals(BigDecimal.valueOf(400), summary.getTotalExpenses());
        assertEquals(BigDecimal.valueOf(600), summary.getNetBalance());
        assertTrue(summary.getCategoryWiseTotals().containsKey("Salary"));
        assertTrue(summary.getCategoryWiseTotals().containsKey("Rent"));
    }
}
