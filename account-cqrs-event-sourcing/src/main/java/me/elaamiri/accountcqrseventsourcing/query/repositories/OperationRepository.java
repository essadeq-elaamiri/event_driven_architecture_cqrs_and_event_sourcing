package me.elaamiri.accountcqrseventsourcing.query.repositories;

import me.elaamiri.accountcqrseventsourcing.query.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
}
