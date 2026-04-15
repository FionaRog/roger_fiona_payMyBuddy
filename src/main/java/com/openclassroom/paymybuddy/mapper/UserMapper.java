package com.openclassroom.paymybuddy.mapper;

import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto (User user);
}
