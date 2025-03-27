package com.rustambek.payment.specification.transaction;

import com.rustambek.payment.model.transactions.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.UUID;

public class TransactionSpecification {
    public static Specification<Transaction> hasUserId(UUID userId) {
        return (root, query, cb) -> userId == null ?
                cb.conjunction() : cb.equal(root.get("userId"), userId);
    }

    public static Specification<Transaction> transactionDateAfter(Date start) {
        return (root, query, cb) -> start == null ?
                cb.conjunction() : cb.greaterThanOrEqualTo(root.get("transactionDate"), start);
    }

    public static Specification<Transaction> transactionDateBefore(Date end) {
        return (root, query, cb) -> end == null ?
                cb.conjunction() : cb.lessThanOrEqualTo(root.get("transactionDate"), end);
    }
}
