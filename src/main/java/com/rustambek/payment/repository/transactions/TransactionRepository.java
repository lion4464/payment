package com.rustambek.payment.repository.transactions;

import com.rustambek.payment.model.transactions.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
    @Query(value = "SELECT nextval('transactions_generate_number_seq')", nativeQuery = true)
    Long nextValGenerateValue();

}
