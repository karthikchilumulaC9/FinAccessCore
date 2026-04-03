package org.example.finaccesscore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finaccesscore.dto.CreateFinancialRecordRequest;
import org.example.finaccesscore.dto.FinancialRecordDTO;
import org.example.finaccesscore.dto.UpdateFinancialRecordRequest;
import org.example.finaccesscore.exception.ResourceNotFoundException;
import org.example.finaccesscore.mapper.FinancialRecordMapper;
import org.example.finaccesscore.model.FinancialRecord;
import org.example.finaccesscore.model.RecordType;
import org.example.finaccesscore.model.User;
import org.example.finaccesscore.repository.FinancialRecordRepository;
import org.example.finaccesscore.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FinancialRecordService {

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
     * Validate user has access to the record.
     * ADMIN: can access all records
     * ANALYST/VIEWER: can only access their own records
     */
    private void validateRecordAccess(FinancialRecord record) {
        if (!isAdmin()) {
            User currentUser = getCurrentUser();
            if (!record.getUser().getId().equals(currentUser.getId())) {
                log.warn("Access denied: user {} attempted to access record {} owned by user {}", 
                    currentUser.getUsername(), record.getId(), record.getUser().getUsername());
                throw new AccessDeniedException("You don't have permission to access this record");
            }
        }
    }

    @Transactional
    public FinancialRecordDTO createRecord(CreateFinancialRecordRequest request) {
        User currentUser = getCurrentUser();
        log.info("Creating financial record for user {}: type={}, category={}, amount={}", 
            currentUser.getUsername(), request.getType(), request.getCategory(), request.getAmount());
        
        FinancialRecord record = financialRecordMapper.toEntity(request);
        record.setUser(currentUser);
        
        FinancialRecordDTO result = financialRecordMapper.toDTO(financialRecordRepository.save(record));
        log.info("Financial record created successfully with id={}", result.getId());
        return result;
    }

    public List<FinancialRecordDTO> getAllRecords() {
        if (isAdmin()) {
            log.debug("Admin fetching all financial records");
            return financialRecordRepository.findByDeletedFalse().stream()
                    .map(financialRecordMapper::toDTO)
                    .collect(Collectors.toList());
        } else {
            User currentUser = getCurrentUser();
            log.debug("User {} fetching their financial records", currentUser.getUsername());
            return financialRecordRepository.findByUserAndDeletedFalse(currentUser).stream()
                    .map(financialRecordMapper::toDTO)
                    .collect(Collectors.toList());
        }
    }

    public FinancialRecordDTO getRecordById(Long id) {
        log.debug("Fetching financial record with id={}", id);
        FinancialRecord record = financialRecordRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Financial record not found with id={}", id);
                    return new ResourceNotFoundException("Financial Record", "id", id);
                });
        
        validateRecordAccess(record);
        return financialRecordMapper.toDTO(record);
    }

    @Transactional
    public FinancialRecordDTO updateRecord(Long id, UpdateFinancialRecordRequest request) {
        log.info("Updating financial record with id={}", id);
        FinancialRecord record = financialRecordRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Financial record not found for update with id={}", id);
                    return new ResourceNotFoundException("Financial Record", "id", id);
                });
        
        validateRecordAccess(record);
        
        financialRecordMapper.applyUpdate(request, record);
        FinancialRecordDTO result = financialRecordMapper.toDTO(financialRecordRepository.save(record));
        log.info("Financial record updated successfully with id={}", id);
        return result;
    }

    @Transactional
    public void deleteRecord(Long id) {
        log.info("Soft deleting financial record with id={}", id);
        FinancialRecord record = financialRecordRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Financial record not found for deletion with id={}", id);
                    return new ResourceNotFoundException("Financial Record", "id", id);
                });
        
        validateRecordAccess(record);
        
        record.setDeleted(true);
        financialRecordRepository.save(record);
        log.info("Financial record soft deleted successfully with id={}", id);
    }

    public List<FinancialRecordDTO> filterRecordsWithDateRange(String category, RecordType type,
                                                                LocalDate startDate, LocalDate endDate) {
        if (isAdmin()) {
            log.debug("Admin filtering records: category={}, type={}, startDate={}, endDate={}", 
                category, type, startDate, endDate);
            return financialRecordRepository.findByFiltersWithDateRange(category, type, startDate, endDate).stream()
                    .map(financialRecordMapper::toDTO)
                    .collect(Collectors.toList());
        } else {
            User currentUser = getCurrentUser();
            log.debug("User {} filtering their records: category={}, type={}, startDate={}, endDate={}", 
                currentUser.getUsername(), category, type, startDate, endDate);
            return financialRecordRepository.findByUserAndFiltersWithDateRange(
                    currentUser, category, type, startDate, endDate).stream()
                    .map(financialRecordMapper::toDTO)
                    .collect(Collectors.toList());
        }
    }

    public Page<FinancialRecordDTO> getAllRecordsPaginated(Pageable pageable) {
        if (isAdmin()) {
            log.debug("Admin fetching paginated records: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
            return financialRecordRepository.findAll(pageable)
                    .map(financialRecordMapper::toDTO);
        } else {
            User currentUser = getCurrentUser();
            log.debug("User {} fetching paginated records: page={}, size={}", 
                currentUser.getUsername(), pageable.getPageNumber(), pageable.getPageSize());
            return financialRecordRepository.findByUserAndDeletedFalse(currentUser, pageable)
                    .map(financialRecordMapper::toDTO);
        }
    }
}
