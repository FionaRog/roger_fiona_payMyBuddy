package com.openclassroom.paymybuddy.mapper;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct chargé de convertir une transaction entre ses différentes représentations.
 *
 * <p>Permet de transformer un {@link TransactionRequestDto} en entité {@link Transaction},
 * et une entité {@link Transaction} en {@link TransactionResponseDto}.</p>
 *
 * L'implémentation est généré automatiquement par MapStruct.
 */
@Mapper(componentModel = "spring", imports = java.time.LocalDateTime.class)
public interface TransactionMapper {

    /**
     * Convertit un DTO de requête en entité {@code Transaction}.
     *
     * L'identifiant de transaction est ignoré car il est généré automatiquement.
     * L'expéditeur et le destinataire sont renseignés via les paramètres fournis.
     * La date de transaction est initialisée avec la date et l'heure courantes.
     *
     * @param dto le DTO contenant les informations de la transaction
     * @param sender l'expéditeur
     * @param receiver le destinataire
     * @return une nouvelle instance de {@link Transaction} prête à être persistée.
     */
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "sender", source = "sender")
    @Mapping(target = "receiver", source = "receiver")
    @Mapping(target = "dateTransaction", expression = "java(LocalDateTime.now())")
    Transaction toEntity(TransactionRequestDto dto, User sender, User receiver);

    /**
     * Convertit une entité {@link Transaction} en DTO de réponse.
     *
     * Le pseudo de l'expéditeur et du destinataire sont extraites depuis les relations
     * {@code sender.username} et {@code sender.username}.
     *
     * @param transaction l'entité transaction à convertir
     * @return un {@link TransactionResponseDto} représentant la transaction
     */
    @Mapping(source = "sender.username", target = "senderUsername")
    @Mapping(source = "receiver.username", target = "receiverUsername")
    TransactionResponseDto toDto(Transaction transaction);
}
