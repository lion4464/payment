package com.rustambek.payment.service.transactions;

import com.rustambek.payment.dto.transactions.TransactionRequest;
import com.rustambek.payment.enums.transactions.PaymentStatusEnum;
import com.rustambek.payment.exception.BalanceNotEnoughException;
import com.rustambek.payment.exception.RecordNotFoundException;
import com.rustambek.payment.mapper.transactions.TransactionMapper;
import com.rustambek.payment.model.transactions.Transaction;
import com.rustambek.payment.model.user.User;
import com.rustambek.payment.repository.transactions.TransactionRepository;
import com.rustambek.payment.service.user.UserService;
import com.rustambek.payment.specification.transaction.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserService userService;
    @Value("${app.transaction.expiration}")
    private Long invoiceExpiration;
    @Value("${app.transaction.prefix}")
    private String prefix;

    @Transactional
    public ResponseEntity<?> create(TransactionRequest transaction) {
        User user = userService.getCurrentUser();
        checkBalanceAndTransactionValue(transaction.getAmount(),user.getBalance());
        user.setBalance(user.getBalance() - transaction.getAmount());
        Transaction model = buildTransaction(transaction, user.getId());
        Transaction savedTr = saveTransaction(model);
        userService.saveViaViod(user);

        //TODO we can save try catch block bz some problems exists during saving proccess
        // we save another table with order
        // and then we can triggered another method to save transactions

        return ResponseEntity.ok(transactionMapper.toDto(savedTr));
    }



     public ResponseEntity<?> getById(UUID id) {
        Transaction model = findById(id);
        return ResponseEntity.ok(transactionMapper.toDto(model));
    }


    public ResponseEntity<?> delete(UUID id) {
    existsById(id);
    transactionRepository.deleteById(id);
    return ResponseEntity.ok("SUCCESS");
    }


    public ResponseEntity<?> pageable(UUID userId, Date start, Date end, Pageable pageable) {
        Specification<Transaction> spec = Specification.where(null);
        spec = spec
                .and(TransactionSpecification.hasUserId(userId))
                .and(TransactionSpecification.transactionDateAfter(start))
                .and(TransactionSpecification.transactionDateBefore(end));
        Page<Transaction> allTransactions = transactionRepository.findAll(spec, pageable);
        return ResponseEntity.ok(transactionMapper.toDtoPage(allTransactions));
    }
    private String generateNewTransactionNumber(String prefix, Date transactionDate, Long generateNumber) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return prefix + df.format(transactionDate) + "-" + String.format("%09d", generateNumber);

    }

    private Transaction findById(UUID id) {
        return transactionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Transaction not found"));
    }
    private Boolean existsById(UUID id) {
        if (!transactionRepository.existsById(id)){
            throw new RecordNotFoundException("Transaction not found");
        }
        return true;
    }
    private void checkBalanceAndTransactionValue(Long reqAmount, Long balance) {
        if (balance-reqAmount<0)
            throw new BalanceNotEnoughException("insufficient balance");
        if (reqAmount==0)
            throw new BalanceNotEnoughException("amount must be greater than 0");
    }

    private Transaction saveTransaction(Transaction tx) {
        return transactionRepository.save(tx);
    }

    private Transaction buildTransaction(TransactionRequest transaction, UUID userId) {
        long genVal = transactionRepository.nextValGenerateValue();
        Date now = new Date();
        return  Transaction.builder()
                .userId(userId)
                .amount(transaction.getAmount())
                .paymentType(transaction.getPaymentType())
                .status(PaymentStatusEnum.SUCCESS)
                .paidAmount(transaction.getAmount())
                .transactionDate(new Date())
                .expiredDate(System.currentTimeMillis() + (invoiceExpiration * 1000))
                .generateNumber(genVal)
                .transactionNumber(generateNewTransactionNumber(prefix,now,genVal))
                .build();
    }

}
