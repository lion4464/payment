package com.rustambek.payment.service.transactions;

import com.rustambek.payment.dto.transactions.TransactionRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.UUID;

public interface TransactionService {
    ResponseEntity<?> create(TransactionRequest transaction);
    ResponseEntity<?> getById(UUID id);
    ResponseEntity<?> pageable(UUID userId, Date start, Date end, Pageable pageable);
    ResponseEntity<?> delete(UUID id);

}
