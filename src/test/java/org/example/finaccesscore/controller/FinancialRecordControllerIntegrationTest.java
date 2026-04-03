package org.example.finaccesscore.controller;

import org.example.finaccesscore.dto.CreateFinancialRecordRequest;
import org.example.finaccesscore.dto.FinancialRecordDTO;
import org.example.finaccesscore.model.RecordType;
import org.example.finaccesscore.repository.FinancialRecordRepository;
import org.example.finaccesscore.service.FinancialRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FinancialRecordControllerIntegrationTest {

    @Autowired
    private FinancialRecordService financialRecordService;

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    @Test
    public void testCreateAndRetrieveRecord() {
        CreateFinancialRecordRequest request = new CreateFinancialRecordRequest();
        request.setAmount(BigDecimal.valueOf(500));
        request.setType(RecordType.INCOME);
        request.setCategory("Bonus");
        request.setDate(LocalDate.now());
        request.setNotes("Year-end bonus");

        FinancialRecordDTO created = financialRecordService.createRecord(request);
        assertNotNull(created);
        assertEquals(BigDecimal.valueOf(500), created.getAmount());
        assertEquals("Bonus", created.getCategory());

        FinancialRecordDTO retrieved = financialRecordService.getRecordById(created.getId());
        assertEquals(created.getId(), retrieved.getId());
    }

    @Test
    public void testFilterRecords() {
        List<FinancialRecordDTO> records = financialRecordService.getAllRecords();
        assertNotNull(records);
    }

    @Test
    public void testSoftDelete() {
        CreateFinancialRecordRequest request = new CreateFinancialRecordRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setType(RecordType.EXPENSE);
        request.setCategory("Test");
        request.setDate(LocalDate.now());

        FinancialRecordDTO created = financialRecordService.createRecord(request);
        Long id = created.getId();

        financialRecordService.deleteRecord(id);

        assertThrows(RuntimeException.class, () -> financialRecordService.getRecordById(id));
    }
}
