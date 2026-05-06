package com.openclassroom.paymybuddy.mapper;

import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);


    @Test
    @DisplayName("Should map user to user response dto")
    void shouldMapUserToUserResponseDto() {
        User user = new User();
        user.setEmail("email");
        user.setBalance(100);
        user.setUsername("username");

        UserResponseDto userResponseDto = userMapper.toDto(user);

        assertEquals(user.getEmail(), userResponseDto.getEmail());
        assertEquals(user.getUsername(), userResponseDto.getUsername());
        assertEquals(user.getBalance(), userResponseDto.getBalance());
    }

    @Test
    @DisplayName("Should return null when user is null")
    void shouldReturnNullWhenUserIsNull() {
        UserResponseDto userResponseDto = userMapper.toDto(null);

        assertNull(userResponseDto);
    }


}
