package me.elaamiri.accountcqrseventsourcing.query.repositories;

import me.elaamiri.accountcqrseventsourcing.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
