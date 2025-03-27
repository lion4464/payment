package com.rustambek.payment.mapper.users;

import com.rustambek.payment.dto.user.UserResponse;
import com.rustambek.payment.generic.GenericAuditMapper;
import com.rustambek.payment.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericAuditMapper<User, UserResponse> {
}
