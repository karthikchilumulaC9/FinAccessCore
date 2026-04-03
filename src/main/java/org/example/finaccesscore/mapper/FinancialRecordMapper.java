package org.example.finaccesscore.mapper;

import org.example.finaccesscore.dto.CreateFinancialRecordRequest;
import org.example.finaccesscore.dto.FinancialRecordDTO;
import org.example.finaccesscore.dto.UpdateFinancialRecordRequest;
import org.example.finaccesscore.model.FinancialRecord;
import org.springframework.stereotype.Component;

@Component
public class FinancialRecordMapper {

    public FinancialRecord toEntity(CreateFinancialRecordRequest request) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());
        return record;
    }

    public void applyUpdate(UpdateFinancialRecordRequest request, FinancialRecord record) {
        if (request.getAmount() != null) {
            record.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            record.setType(request.getType());
        }
        if (request.getCategory() != null) {
            record.setCategory(request.getCategory());
        }
        if (request.getDate() != null) {
            record.setDate(request.getDate());
        }
        if (request.getNotes() != null) {
            record.setNotes(request.getNotes());
        }
    }

    public FinancialRecordDTO toDTO(FinancialRecord record) {
        FinancialRecordDTO dto = new FinancialRecordDTO();
        dto.setId(record.getId());
        dto.setAmount(record.getAmount());
        dto.setType(record.getType());
        dto.setCategory(record.getCategory());
        dto.setDate(record.getDate());
        dto.setNotes(record.getNotes());
        
        // Safely handle user relationship
        if (record.getUser() != null) {
            dto.setUserId(record.getUser().getId());
            dto.setUsername(record.getUser().getUsername());
        }
        
        return dto;
    }
}