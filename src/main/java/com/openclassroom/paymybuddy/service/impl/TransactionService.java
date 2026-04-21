package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.exception.InvalidTransactionException;
import com.openclassroom.paymybuddy.exception.UserNotFoundException;
import com.openclassroom.paymybuddy.mapper.TransactionMapper;
import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.TransactionRepository;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation du service de gestion des transactions entre utilisateurs.
 *
 * <p>Ce service permet d'ajouter une nouvelle transaction(envoi d'un montant depuis un utilisateur vers un autre)
 * et de récupérer l'historique des transactions d'un utilisateur donné.</p>
 *
 * <p>Les règles métiers incluent:</p>
 * <ul>
 *     <li>existence de l'expéditeur et du destinataire,</li>
 *     <li>montant positif,</li>
 *     <li>solde suffisant pour effectuer la transaction côté expéditeur,</li>
 *     <li>le destinataire doit figurer dans la liste de contact de l'expéditeur.</li>
 * </ul>
 */

@Service
public class TransactionService implements ITransactionService {
    /**
     * Repository permettant la persistance et la récupération des transactions.
     */
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Repository permettant la récupération des utilisateurs par leur email.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Mapper permettant une conversion entre les entités {@link Transaction} et
     * les DTOs {@link TransactionRequestDto} / {@link TransactionResponseDto}.
     */
    @Autowired
    private TransactionMapper transactionMapper;


    /**
     * Ajoute une nouvelle transaction entre deux utilisateurs.
     *
     * <p>En cas de succès, la transaction est enregistrée en base de donnée,
     * les soldes des 2 utilisateurs sont mis à jour, et
     * la transaction est retournée sous forme de {@link TransactionResponseDto}.</p>
     *
     * @param senderEmail l'email de l'expéditeur (non {@code null})
     * @param requestDto le DTO contenant les informations de la transaction (non {@code null})
     * @return un {@link TransactionResponseDto} représentant la transaction enregistrée
     *
     * @throws UserNotFoundException si l'expéditeur ou le destinataire n'existe pas
     * @throws InvalidTransactionException  si :
     *          <ul>
     *            <li>le montant est inférieur ou égal à 0,</li>
     *            <li>l'expéditeur n'a pas suffisament de solde,</li>
     *            <li>l'expéditeur et le destinataire sont identiques,</li>
     *            <li>le destinataire n'apparaît pas dans la liste d'amis de l'expéditeur.</li>
     *          </ul>
     */
    public TransactionResponseDto addTransaction(String senderEmail, TransactionRequestDto requestDto) {

        User sender = userRepository.findByEmail(senderEmail).
                orElseThrow(() -> new UserNotFoundException("Sender not found"));

        User receiver = userRepository.findByEmail(requestDto.getReceiverEmail()).
                orElseThrow(() -> new UserNotFoundException("Receiver not found"));

        double amount = requestDto.getAmount();
        if(amount <= 0 ) {
            throw new InvalidTransactionException("Amount must be superior to 0");
        }
        if (sender.getBalance() < amount) {
            throw new InvalidTransactionException("Insufficient balance");
        }

        if(sender.getId() == receiver.getId()) {
            throw new InvalidTransactionException("Sender and receiver must be different");
        }

        int relationCount = userRepository.verifyRelation(sender.getId(), receiver.getId());
        if (relationCount == 0) {
            throw new InvalidTransactionException("Receiver must be in sender's friends list");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        Transaction transaction = transactionMapper.toEntity(requestDto, sender, receiver);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toDto(savedTransaction);
    }

    /**
     * Récupère la liste de toutes les transactions (envoyées et reçues)
     * associées à un utilisateur identifié par son email.
     * <p>
     * Les transactions retournées sont converties en {@link TransactionResponseDto}
     * à l'aide du mapper transaction.
     * @param email l'email de l'utilisateur dont on souhaite obtenir l'historique des
     *              transactions (non {@code null})
     * @return une liste de {@link TransactionResponseDto} représentant toutes les
     *         transactions envoyées et reçues par l'utilisateur
     * @throws UserNotFoundException si aucun utilisateur n'est trouvé avec cet email
     */
    public List<TransactionResponseDto> getUserTransactions(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Transaction> sent = transactionRepository.findBySender(user);
        List<Transaction> received = transactionRepository.findByReceiver(user);

        List<Transaction> all = new ArrayList<>();
        all.addAll(sent);
        all.addAll(received);

        return all.stream()
                .map(transactionMapper::toDto)
                .toList();
    }
}
