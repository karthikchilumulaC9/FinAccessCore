package org.example.finaccesscore.controller;

import org.example.finaccesscore.dto.CategoryTotalDTO;
import org.example.finaccesscore.dto.DashboardSummaryDTO;
import org.example.finaccesscore.dto.FinancialRecordDTO;
import org.example.finaccesscore.dto.TrendPointDTO;
import org.example.finaccesscore.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DashboardControllerIntegrationTest {

    @Autowired
    private DashboardService dashboardService;

    @Test
    public void testGetDashboardSummary() {
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
        assertNotNull(summary);
        assertNotNull(summary.getTotalIncome());
        assertNotNull(summary.getTotalExpenses());
        assertNotNull(summary.getNetBalance());
        assertNotNull(summary.getCategoryWiseTotals());
    }

    @Test
    public void testGetCategoryTotals() {
        List<CategoryTotalDTO> categoryTotals = dashboardService.getCategoryTotals();
        assertNotNull(categoryTotals);
    }

    @Test
    public void testGetRecentActivity() {
        List<FinancialRecordDTO> recent = dashboardService.getRecentActivity(5);
        assertNotNull(recent);
        assertTrue(recent.size() <= 5);
    }

    @Test
    public void testGetMonthlyTrends() {
        List<TrendPointDTO> trends = dashboardService.getMonthlyTrends();
        assertNotNull(trends);
    }
}
