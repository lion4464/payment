package com.rustambek.payment.model.transactions;

import com.rustambek.payment.enums.transactions.CommissionTypeEnum;
import com.rustambek.payment.generic.GenericAuditingEntity;
import com.rustambek.payment.enums.transactions.PaymentStatusEnum;
import com.rustambek.payment.enums.transactions.PaymentTypeEnum;
import com.rustambek.payment.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "transactions")
@SQLDelete(sql = "update transactions set deleted = 'true' where id = ?")
@Where(clause = "deleted = 'false'")
public class Transaction extends GenericAuditingEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "paid_amount", nullable = false)
    private Long paidAmount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentTypeEnum paymentType = PaymentTypeEnum.CASH;


    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;


    @Column(name = "expired_date", nullable = false)
    private Long expiredDate;

    @Column(name = "transaction_number", unique = true, nullable = false, updatable = false)
    private String transactionNumber;

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_number_generator")
    @SequenceGenerator(
            name = "transaction_number_generator",
            sequenceName = "transaction_generated_number_seq",
            allocationSize = 1
    )
    @Column(name = "generate_number", unique = true, nullable = false, updatable = false, columnDefinition="serial")
    private Long generateNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private UUID userId;


}
