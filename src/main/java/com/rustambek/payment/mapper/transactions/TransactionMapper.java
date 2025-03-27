package com.rustambek.payment.mapper.transactions;

import com.rustambek.payment.dto.transactions.TransactionResponse;
import com.rustambek.payment.generic.GenericAuditMapper;
import com.rustambek.payment.model.transactions.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface TransactionMapper extends GenericAuditMapper<Transaction, TransactionResponse> {
}
