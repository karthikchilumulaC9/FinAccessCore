package org.example.finaccesscore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finaccesscore.dto.CategoryTotalDTO;
import org.example.finaccesscore.dto.DashboardSummaryDTO;
import org.example.finaccesscore.dto.FinancialRecordDTO;
import org.example.finaccesscore.dto.TrendPointDTO;
import org.example.finaccesscore.exception.ResourceNotFoundException;
import org.example.finaccesscore.mapper.FinancialRecordMapper;
import org.example.finaccesscore.model.FinancialRecord;
import org.example.finaccesscore.model.RecordType;
import org.example.finaccesscore.model.User;
import org.example.finaccesscore.repository.FinancialRecordRepository;
import org.example.finaccesscore.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final FinancialRecordRepository financialRecordRepository;
    private final FinancialRecordMapper financialRecordMapper;
    private final UserRepository userRepository;

    /**
     * Get current authenticated user from security context.
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    /**
     * Check if current user is admin.
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Get records based on user role.
     * ADMIN: all records
     * Others: only their own records
     */
    private List<FinancialRecord> getUserRecords() {
        if (isAdmin()) {
            log.debug("Admin fetching all records for dashboard");
            return financialRecordRepository.findByDeletedFalse();
        } else {
            User currentUser = getCurrentUser();
            log.debug("User {} fetching their records for dashboard", currentUser.getUsername());
            return financialRecordRepository.findByUserAndDeletedFalse(currentUser);
        }
    }

    public DashboardSummaryDTO getDashboardSummary() {
        List<FinancialRecord> allRecords = getUserRecords();

        BigDecimal totalIncome = calculateTotal(allRecords, RecordType.INCOME);
        BigDecimal totalExpenses = calculateTotal(allRecords, RecordType.EXPENSE);

        Map<String, BigDecimal> categoryWiseTotals = allRecords.stream()
                .collect(Collectors.groupingBy(
                        FinancialRecord::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)
                ));

        List<FinancialRecordDTO> recentActivity = allRecords.stream()
                .sorted((r1, r2) -> r2.getDate().compareTo(r1.getDate()))
                .limit(5)
                .map(financialRecordMapper::toDTO)
                .collect(Collectors.toList());

        List<TrendPointDTO> monthlyTrends = calculateMonthlyTrends(allRecords);

        return DashboardSummaryDTO.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(totalIncome.subtract(totalExpenses))
                .categoryWiseTotals(categoryWiseTotals)
                .recentActivity(recentActivity)
                .monthlyTrends(monthlyTrends)
                .build();
    }

    private BigDecimal calculateTotal(List<FinancialRecord> records, RecordType type) {
        return records.stream()
                .filter(r -> r.getType() == type)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<CategoryTotalDTO> getCategoryTotals() {
        List<FinancialRecord> allRecords = getUserRecords();

        Map<String, BigDecimal> categoryWiseTotals = allRecords.stream()
                .collect(Collectors.groupingBy(
                        FinancialRecord::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)
                ));

        return categoryWiseTotals.entrySet().stream()
                .map(entry -> new CategoryTotalDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<FinancialRecordDTO> getRecentActivity(int limit) {
        List<FinancialRecord> allRecords = getUserRecords();
        return allRecords.stream()
                .sorted((r1, r2) -> r2.getDate().compareTo(r1.getDate()))
                .limit(limit)
                .map(financialRecordMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TrendPointDTO> getMonthlyTrends() {
        List<FinancialRecord> allRecords = getUserRecords();
        return calculateMonthlyTrends(allRecords);
    }

    private List<TrendPointDTO> calculateMonthlyTrends(List<FinancialRecord> records) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, List<FinancialRecord>> groupedByMonth = records.stream()
                .collect(Collectors.groupingBy(r -> r.getDate().format(formatter)));

        return groupedByMonth.entrySet().stream()
                .map(entry -> {
                    BigDecimal income = calculateTotal(entry.getValue(), RecordType.INCOME);
                    BigDecimal expense = calculateTotal(entry.getValue(), RecordType.EXPENSE);
                    return new TrendPointDTO(entry.getKey(), income, expense);
                })
                .sorted((t1, t2) -> t1.getPeriod().compareTo(t2.getPeriod()))
                .collect(Collectors.toList());
    }
}
