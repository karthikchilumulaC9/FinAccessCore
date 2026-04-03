package org.example.finaccesscore.repository;

import org.example.finaccesscore.model.FinancialRecord;
import org.example.finaccesscore.model.RecordType;
import org.example.finaccesscore.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {
    List<FinancialRecord> findByDeletedFalse();
    
    List<FinancialRecord> findByUserAndDeletedFalse(User user);
    
    Page<FinancialRecord> findByUserAndDeletedFalse(User user, Pageable pageable);
    
    Optional<FinancialRecord> findByIdAndDeletedFalse(Long id);

    @Query("SELECT r FROM FinancialRecord r WHERE r.deleted = false " +
           "AND (:category IS NULL OR r.category = :category) " +
           "AND (:type IS NULL OR r.type = :type) " +
           "AND (:startDate IS NULL OR r.date >= :startDate) " +
           "AND (:endDate IS NULL OR r.date <= :endDate)")
    List<FinancialRecord> findByFiltersWithDateRange(
            @Param("category") String category,
            @Param("type") RecordType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT r FROM FinancialRecord r WHERE r.user = :user AND r.deleted = false " +
           "AND (:category IS NULL OR r.category = :category) " +
           "AND (:type IS NULL OR r.type = :type) " +
           "AND (:startDate IS NULL OR r.date >= :startDate) " +
           "AND (:endDate IS NULL OR r.date <= :endDate)")
    List<FinancialRecord> findByUserAndFiltersWithDateRange(
            @Param("user") User user,
            @Param("category") String category,
            @Param("type") RecordType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
