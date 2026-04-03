package org.example.finaccesscore.service;

import org.example.finaccesscore.dto.CreateFinancialRecordRequest;
import org.example.finaccesscore.dto.FinancialRecordDTO;
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
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class FinancialRecordServiceTest {

    @Autowired
    private FinancialRecordService financialRecordService;

    @MockitoBean
    private FinancialRecordRepository financialRecordRepository;

    @Test
    public void testCreateRecord() {
        CreateFinancialRecordRequest request = new CreateFinancialRecordRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setCategory("Test Category");
        request.setType(RecordType.INCOME);
        request.setDate(LocalDate.now());

        FinancialRecord record = new FinancialRecord();
        record.setAmount(BigDecimal.valueOf(100));
        record.setCategory("Test Category");

        Mockito.when(financialRecordRepository.save(any(FinancialRecord.class))).thenReturn(record);

        FinancialRecordDTO createdRecord = financialRecordService.createRecord(request);
        assertNotNull(createdRecord);
        assertEquals(BigDecimal.valueOf(100), createdRecord.getAmount());
    }

    @Test
    public void testGetRecordById() {
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        record.setAmount(BigDecimal.valueOf(100));
        record.setDeleted(false);

        Mockito.when(financialRecordRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(record));

        FinancialRecordDTO retrievedRecord = financialRecordService.getRecordById(1L);
        assertNotNull(retrievedRecord);
        assertEquals(BigDecimal.valueOf(100), retrievedRecord.getAmount());
    }
}
