package com.rustambek.payment.model.user;

import com.rustambek.payment.enums.user.UserRole;
import com.rustambek.payment.generic.GenericAuditingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "users")
@SQLDelete(sql = "update users set deleted = 'true' where id = ?")
@Where(clause = "deleted = 'false'")
public class User extends GenericAuditingEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "username",nullable = false,unique = true)
    private String username;
    private String password;
    private String fullName;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Long balance = 0L;
}
