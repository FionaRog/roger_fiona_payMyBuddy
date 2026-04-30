package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.impl.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private UserRepository userRepository;

    private IBalanceService balanceService;

    @BeforeEach
    void setUp() {
        balanceService = new BalanceService(userRepository);
    }

    @Test
    @DisplayName("Should increase user balance when deposit is valid")
    void shouldIncreaseBalanceWhenDepositIsValid() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setBalance(100.0);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        balanceService.deposit("test@mail.com", 50.0);

        assertEquals(150.0, user.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when deposit amount is zero or negative")
    void shouldThrowExceptionWhenDepositAmountIsInvalid() {

        assertThrows(BusinessException.class, () ->
                balanceService.deposit("test@mail.com", 0)
        );

        assertThrows(BusinessException.class, () ->
                balanceService.deposit("test@mail.com", -10)
        );
    }

    @Test
    @DisplayName("Should decrease user balance when withdraw is valid")
    void shouldDecreaseBalanceWhenWithdrawIsValid() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setBalance(100.0);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        balanceService.withdraw("test@mail.com", 40.0);

        assertEquals(60.0, user.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when balance is insufficient")
    void shouldThrowExceptionWhenBalanceIsInsufficient() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setBalance(50.0);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () ->
                balanceService.withdraw("test@mail.com", 100.0)
        );
    }

    @Test
    @DisplayName("Should throw exception when withdraw amount is invalid")
    void shouldThrowExceptionWhenWithdrawAmountIsInvalid() {

        assertThrows(BusinessException.class, () ->
                balanceService.withdraw("test@mail.com", 0)
        );

        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                balanceService.withdraw("test@mail.com", 50.0)
        );
    }
}
