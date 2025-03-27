package com.rustambek.payment.dto.transactions;

import com.rustambek.payment.enums.transactions.PaymentStatusEnum;
import com.rustambek.payment.enums.transactions.PaymentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private UUID id;
    private PaymentStatusEnum status;
    private Long amount;
    private Long paidAmount = 0L;
    private PaymentTypeEnum paymentType;
    private Date transactionDate;
    private Long expiredDate;
    private String transactionNumber;
    private UUID userId;
}
