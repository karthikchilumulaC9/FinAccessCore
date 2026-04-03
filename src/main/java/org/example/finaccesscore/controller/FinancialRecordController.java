package org.example.finaccesscore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.finaccesscore.dto.CreateFinancialRecordRequest;
import org.example.finaccesscore.dto.FinancialRecordDTO;
import org.example.finaccesscore.dto.UpdateFinancialRecordRequest;
import org.example.finaccesscore.model.RecordType;
import org.example.finaccesscore.service.FinancialRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Validated
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<FinancialRecordDTO> createRecord(@Valid @RequestBody CreateFinancialRecordRequest request) {
        return ResponseEntity.status(201).body(financialRecordService.createRecord(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<FinancialRecordDTO> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(financialRecordService.getRecordById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<FinancialRecordDTO> updateRecord(@PathVariable Long id, @Valid @RequestBody UpdateFinancialRecordRequest request) {
        return ResponseEntity.ok(financialRecordService.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        financialRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<List<FinancialRecordDTO>> getAllOrFilterRecords(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (category == null && type == null && startDate == null && endDate == null) {
            return ResponseEntity.ok(financialRecordService.getAllRecords());
        }
        return ResponseEntity.ok(financialRecordService.filterRecordsWithDateRange(category, type, startDate, endDate));
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<Page<FinancialRecordDTO>> getAllRecordsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(financialRecordService.getAllRecordsPaginated(pageable));
    }
}
