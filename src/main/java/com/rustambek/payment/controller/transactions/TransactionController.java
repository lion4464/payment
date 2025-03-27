package com.rustambek.payment.controller.transactions;

import com.rustambek.payment.dto.transactions.TransactionRequest;
import com.rustambek.payment.service.transactions.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping( "/api/transaction")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest transaction) {
        return transactionService.create(transaction);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable UUID id) {
        return transactionService.getById(id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable UUID id) {
        return transactionService.delete(id);
    }
    @GetMapping
    public ResponseEntity<?> getTransactions(
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end,
            Pageable pageable) {
        return transactionService.pageable(userId,start,end, pageable);
    }
}
