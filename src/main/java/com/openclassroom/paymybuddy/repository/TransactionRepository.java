package com.openclassroom.paymybuddy.repository;

import com.openclassroom.paymybuddy.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//pourquoi Integer?
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
}
