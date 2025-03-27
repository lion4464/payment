package com.rustambek.payment.dto.transactions;

import com.rustambek.payment.enums.transactions.PaymentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private Long amount;

    @NotNull(message = "Payment type must be specified")
    private PaymentTypeEnum paymentType;
}
