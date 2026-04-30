package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.mapper.TransactionMapper;
import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.TransactionRepository;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.impl.TransactionService;
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
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionMapper transactionMapper;

    private ITransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository, userRepository, transactionMapper);
    }

    @Test
    @DisplayName("Should create transaction and update balances with fee")
    void shouldCreateTransactionAndUpdateBalancesWithFee() {

        User sender = new User();
        sender.setId(1);
        sender.setEmail("sender@mail.com");
        sender.setUsername("sender");
        sender.setBalance(200.0);

        User receiver = new User();
        receiver.setId(2);
        receiver.setEmail("receiver@mail.com");
        receiver.setUsername("receiver");
        receiver.setBalance(50.0);

        TransactionRequestDto request = new TransactionRequestDto();
        request.setReceiverEmail("receiver@mail.com");
        request.setAmount(100.0);
        request.setDescription("Test");

        when(userRepository.findByEmail("sender@mail.com"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findByEmail("receiver@mail.com"))
                .thenReturn(Optional.of(receiver));

        when(userRepository.verifyRelation(1, 2))
                .thenReturn(1);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(transactionMapper.toEntity(any(), any(), any()))
                .thenAnswer(invocation -> {
                    Transaction t = new Transaction();
                    t.setAmount(100.0);
                    return t;
                });

        when(transactionMapper.toDto(any()))
                .thenReturn(new TransactionResponseDto());

        transactionService.addTransaction("sender@mail.com", request);

        double expectedFee = 100.0 * 0.05;
        double expectedTotal = 100.0 + expectedFee;

        assertEquals(200.0 - expectedTotal, sender.getBalance());
        assertEquals(50.0 + 100.0, receiver.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when transaction amount is invalid")
    void shouldThrowExceptionWhenTransactionAmountIsInvalid() {

        TransactionRequestDto request = new TransactionRequestDto();
        request.setReceiverEmail("receiver@mail.com");
        request.setAmount(0);

        User sender = new User();
        sender.setEmail("sender@mail.com");

        User receiver = new User();
        receiver.setEmail("receiver@mail.com");

        when(userRepository.findByEmail("sender@mail.com"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findByEmail("receiver@mail.com"))
                .thenReturn(Optional.of(receiver));

        assertThrows(BusinessException.class, () ->
                transactionService.addTransaction("sender@mail.com", request)
        );

        verify(transactionRepository, never()).save(any(Transaction.class));
    }@Test
    @DisplayName("Should throw exception when sender balance is insufficient including fee")
    void shouldThrowExceptionWhenSenderBalanceIsInsufficientIncludingFee() {

        User sender = new User();
        sender.setId(1);
        sender.setEmail("sender@mail.com");
        sender.setBalance(100.0);

        User receiver = new User();
        receiver.setId(2);
        receiver.setEmail("receiver@mail.com");

        TransactionRequestDto request = new TransactionRequestDto();
        request.setReceiverEmail("receiver@mail.com");
        request.setAmount(100.0);

        when(userRepository.findByEmail("sender@mail.com"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findByEmail("receiver@mail.com"))
                .thenReturn(Optional.of(receiver));

        assertThrows(BusinessException.class, () ->
                transactionService.addTransaction("sender@mail.com", request)
        );

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when sender and receiver are the same user")
    void shouldThrowExceptionWhenSenderAndReceiverAreSameUser() {

        User sender = new User();
        sender.setId(1);
        sender.setEmail("sender@mail.com");
        sender.setBalance(200.0);

        User receiver = new User();
        receiver.setId(1);
        receiver.setEmail("sender@mail.com");

        TransactionRequestDto request = new TransactionRequestDto();
        request.setReceiverEmail("sender@mail.com");
        request.setAmount(50.0);

        when(userRepository.findByEmail("sender@mail.com"))
                .thenReturn(Optional.of(sender));

        assertThrows(BusinessException.class, () ->
                transactionService.addTransaction("sender@mail.com", request)
        );

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when receiver is not in sender friends")
    void shouldThrowExceptionWhenReceiverIsNotInSenderFriends() {

        User sender = new User();
        sender.setId(1);
        sender.setEmail("sender@mail.com");
        sender.setBalance(200.0);

        User receiver = new User();
        receiver.setId(2);
        receiver.setEmail("receiver@mail.com");

        TransactionRequestDto request = new TransactionRequestDto();
        request.setReceiverEmail("receiver@mail.com");
        request.setAmount(50.0);

        when(userRepository.findByEmail("sender@mail.com"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findByEmail("receiver@mail.com"))
                .thenReturn(Optional.of(receiver));

        when(userRepository.verifyRelation(1, 2))
                .thenReturn(0);

        assertThrows(BusinessException.class, () ->
                transactionService.addTransaction("sender@mail.com", request)
        );

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when receiver is not found")
    void shouldThrowExceptionWhenReceiverNotFound() {

        User sender = new User();
        sender.setId(1);
        sender.setEmail("sender@mail.com");

        TransactionRequestDto request = new TransactionRequestDto();
        request.setReceiverEmail("receiver@mail.com");
        request.setAmount(50.0);

        when(userRepository.findByEmail("sender@mail.com"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findByEmail("receiver@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                transactionService.addTransaction("sender@mail.com", request)
        );

        verify(userRepository, never()).verifyRelation(anyInt(), anyInt());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when sender is not found")
    void shouldThrowExceptionWhenSenderNotFound() {

        TransactionRequestDto request = new TransactionRequestDto();
        request.setReceiverEmail("receiver@mail.com");
        request.setAmount(50.0);

        when(userRepository.findByEmail("sender@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                transactionService.addTransaction("sender@mail.com", request)
        );

        verify(userRepository, never()).verifyRelation(anyInt(), anyInt());
        verify(transactionRepository, never()).save(any());
    }
}
