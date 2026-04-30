package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.dto.UpdatePasswordRequestDto;
import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.mapper.UserMapper;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private IUserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, userMapper);
    }

// -------------- GET USER PROFILE ----------------
    @Test
    @DisplayName("Should return user profile when user exists")
    void shouldReturnUserProfileWhenUserExists() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setUsername("Test");

        UserResponseDto dto = new UserResponseDto();
        dto.setEmail("test@mail.com");
        dto.setUsername("Test");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(userMapper.toDto(user))
                .thenReturn(dto);

        UserResponseDto result = userService.getUserProfile("test@mail.com");

        assertEquals("test@mail.com", result.getEmail());
        assertEquals("Test", result.getUsername());

        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("Should throw exception when user profile is not found")
    void shouldThrowExceptionWhenUserProfileNotFound() {

        when(userRepository.findByEmail("unknown@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                userService.getUserProfile("unknown@mail.com")
        );

        verify(userMapper, never()).toDto(any());
    }
// ------------- ADD USER---------------
    @Test
    @DisplayName("Should create user with encoded password")
    void shouldCreateUserWithEncodedPassword() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("Password");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("Password"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.addUser(user);

        assertEquals("encodedPassword", result.getPassword());

        verify(passwordEncoder).encode("Password");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(BusinessException.class, () ->
                userService.addUser(user)
        );

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
//-------------- UPDATE PASSWORD --------------------
    @Test
    @DisplayName("Should throw exception when current password is incorrect")
    void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("oldPassword");

        UpdatePasswordRequestDto request = new UpdatePasswordRequestDto();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword");
        request.setConfirmPassword("newPassword");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongPassword", "oldPassword"))
                .thenReturn(false);

        assertThrows(BusinessException.class, () ->
                userService.updatePassword("test@mail.com", request)
        );

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Should throw exception when new password is empty")
    void shouldThrowExceptionWhenNewPasswordIsEmpty() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("encodedOldPassword");

        UpdatePasswordRequestDto request = new UpdatePasswordRequestDto();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("");
        request.setConfirmPassword("");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        assertThrows(BusinessException.class, () ->
                userService.updatePassword("test@mail.com", request)
        );

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Should throw exception when password confirmation does not match")
    void shouldThrowExceptionWhenPasswordConfirmationDoesNotMatch() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("encodedOldPassword");

        UpdatePasswordRequestDto request = new UpdatePasswordRequestDto();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword");
        request.setConfirmPassword("differentPassword");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        assertThrows(BusinessException.class, () ->
                userService.updatePassword("test@mail.com", request)
        );

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Should throw exception when new password is same as old password")
    void shouldThrowExceptionWhenNewPasswordIsSameAsOldPassword() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("encodedOldPassword");

        UpdatePasswordRequestDto request = new UpdatePasswordRequestDto();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("oldPassword");
        request.setConfirmPassword("oldPassword");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        assertThrows(BusinessException.class, () ->
                userService.updatePassword("test@mail.com", request)
        );

        verify(passwordEncoder, never()).encode(any());
    }
//------------- ADD FRIEND ----------------------
    @Test
    @DisplayName("Should add friend when relation does not already exist")
    void shouldAddFriendWhenRelationDoesNotAlreadyExist() {

        User user = new User();
        user.setId(1);
        user.setEmail("user@mail.com");
        user.setFriends(new ArrayList<>());

        User friend = new User();
        friend.setId(2);
        friend.setEmail("friend@mail.com");

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("friend@mail.com"))
                .thenReturn(Optional.of(friend));

        when(userRepository.verifyRelation(1, 2))
                .thenReturn(0);

        userService.addFriend("user@mail.com", "friend@mail.com");

        assertTrue(user.getFriends().contains(friend));

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw exception when friend is not found")
    void shouldThrowExceptionWhenFriendNotFound() {

        User user = new User();
        user.setId(1);
        user.setEmail("user@mail.com");

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("friend@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                userService.addFriend("user@mail.com", "friend@mail.com")
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user tries to add themselves")
    void shouldThrowExceptionWhenAddingSelf() {

        User user = new User();
        user.setId(1);
        user.setEmail("user@mail.com");

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () ->
                userService.addFriend("user@mail.com", "user@mail.com")
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when friend is already added")
    void shouldThrowExceptionWhenFriendAlreadyExists() {

        User user = new User();
        user.setId(1);
        user.setEmail("user@mail.com");
        user.setFriends(new ArrayList<>());

        User friend = new User();
        friend.setId(2);
        friend.setEmail("friend@mail.com");

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("friend@mail.com"))
                .thenReturn(Optional.of(friend));

        when(userRepository.verifyRelation(1, 2))
                .thenReturn(1);

        assertThrows(BusinessException.class, () ->
                userService.addFriend("user@mail.com", "friend@mail.com")
        );

        verify(userRepository, never()).save(any());
    }
// ---------------UPDATE USERNAME---------------
    @Test
    @DisplayName("Should update username when input is valid")
    void shouldUpdateUsernameWhenValid() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setUsername("oldUsername");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        userService.updateUsername("test@mail.com", "newUsername");

        assertEquals("newUsername", user.getUsername());

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw exception when username is empty")
    void shouldThrowExceptionWhenUsernameIsEmpty() {

        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () ->
                userService.updateUsername("test@mail.com", "")
        );

        verify(userRepository, never()).save(any());
    }
// --------------- GET FRIEND USERNAMES -------------
    @Test
    @DisplayName("Should return list of friends")
    void shouldReturnListOfFriends() {

        User user = new User();
        user.setEmail("test@mail.com");

        User friend1 = new User();
        friend1.setEmail("friend1@mail.com");

        User friend2 = new User();
        friend2.setEmail("friend2@mail.com");

        user.setFriends(List.of(friend1, friend2));

        when(userRepository.findByEmailWithFriends("test@mail.com"))
                .thenReturn(Optional.of(user));

        List<User> result = userService.getFriendUsernames("test@mail.com");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should throw exception when user not found for friends list")
    void shouldThrowExceptionWhenUserNotFoundForFriendsList() {

        when(userRepository.findByEmailWithFriends("test@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                userService.getFriendUsernames("test@mail.com")
        );
    }
}

