package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.mapper.TransactionMapper;
import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.TransactionRepository;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.ITransactionService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
     * @throws BusinessException  si :
     *          <ul>
     *              <li>l'expéditeur ou le destinatair n'existe pas</li>
     *            <li>le montant est inférieur ou égal à 0,</li>
     *            <li>l'expéditeur n'a pas suffisament de solde,</li>
     *            <li>l'expéditeur et le destinataire sont identiques,</li>
     *            <li>le destinataire n'apparaît pas dans la liste d'amis de l'expéditeur.</li>
     *          </ul>
     */
    @Transactional
    public TransactionResponseDto addTransaction(String senderEmail, TransactionRequestDto requestDto) {

        log.info("ADD_TRANSACTION_INIT - Début de la transaction de {} vers {}, montant : {}",
                senderEmail, requestDto.getReceiverEmail(), requestDto.getAmount());

        User sender = userRepository.findByEmail(senderEmail).
                orElseThrow(() -> new BusinessException("USER_NOT_FOUND","Utilisateur expéditeur introuvable"));

        User receiver = userRepository.findByEmail(requestDto.getReceiverEmail()).
                orElseThrow(() -> new BusinessException("USER_NOT_FOUND","Utilisateur destinataire introuvable"));

        double amount = requestDto.getAmount();

        if(amount <= 0 ) {
            log.warn("ADD_TRANSACTION_INVALID_AMOUNT - Montant de la transaction {} invalide (email={})",
                    amount, senderEmail);
            throw new BusinessException("INVALID_TRANSACTION", "Le montant doit être supérieur à 0.00");
        }

        if (sender.getBalance() < amount) {
            log.warn("ADD_TRANSACTION_INVALID_BALANCE - Solde insuffisant pour l'expéditeur {} : solde={}, montant={}",
                    senderEmail, sender.getBalance(), amount);
            throw new BusinessException("INVALID_TRANSACTION", "Solde insuffisant");
        }

        if(sender.getId() == receiver.getId()) {
            log.warn("ADD_TRANSACTION_SELF - Tentative de transaction vers soi-même pour l'utilisateur '{}'",
                    senderEmail);
            throw new BusinessException("INVALID_TRANSACTION","L'expéditeur et le destinataire doivent être différents");
        }

        int relationCount = userRepository.verifyRelation(sender.getId(), receiver.getId());
        if (relationCount == 0) {
            log.warn("ADD_TRANSACTION_INVALID_FRIENDS - Le destinataire {} n'est pas dans les relations de {}",
                    receiver.getEmail(), senderEmail);
            throw new BusinessException("INVALID_TRANSACTION","Le destinataire doit être dans la liste de relations");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);
        log.debug("ADD_TRANSACTION_BALANCE_UPDATED - Soldes mis à jour pour l'expéditeur {} et le destinataire {}, montant={}",
                senderEmail, receiver.getEmail(), amount);

        Transaction transaction = transactionMapper.toEntity(requestDto, sender, receiver);
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("ADD_TRANSACTION_SUCCESS - Transaction de {} vers {} créée par {}, id={}",
                amount, receiver.getEmail(), senderEmail, savedTransaction.getTransactionId());
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
     * @throws BusinessException si aucun utilisateur n'est trouvé avec cet email
     */
    public List<TransactionResponseDto> getUserTransactions(String email) {

        log.info("GET_USER_TRANSACTIONS - Récupération de l'historique des transactions pour l'utilisateur {}",
                email);
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new BusinessException("USER_NOT_FOUND","Utilisateur introuvable"));

        List<Transaction> sent = transactionRepository.findBySender(user);
        List<Transaction> received = transactionRepository.findByReceiver(user);

        log.debug("GET_USER_TRANSACTIONS_SIZE - Transactions pour l'utilisateur {}: envoyées={}, reçues={}",
                email, sent.size(), received.size());
        List<Transaction> all = new ArrayList<>();
        all.addAll(sent);
        all.addAll(received);

        log.info("GET_USER_TRANSACTIONS_SUCCESS - Nombre total de transactions récupérées : {} pour l'utilisateur {}",
                all.size(), email);
        return all.stream()
                .map(transactionMapper::toDto)
                .toList();
    }
}
