package com.github.ajharry69.testsoap.transactions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Stream<Transaction> findByStatusInOrderByDateUpdatedAsc(@NonNull Collection<Transaction.Status> statuses);

    long countByStatusIn(@NonNull Collection<Transaction.Status> statuses);
}