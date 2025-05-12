package com.margarib.tictactoe_spring.datasource.mapper;

import com.margarib.tictactoe_spring.datasource.model.UserEntity;
import com.margarib.tictactoe_spring.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserEntityMapper {
    UserEntity toUserEntity(User user);
    User toUser(UserEntity userEntity);
}
