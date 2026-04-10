package com.openclassroom.paymybuddy.repository;

import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

List<Transaction> findBySender(User sender);

List<Transaction> findByReceiver(User receiver);
}
