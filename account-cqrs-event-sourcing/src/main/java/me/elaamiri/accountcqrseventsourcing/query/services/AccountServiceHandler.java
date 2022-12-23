package me.elaamiri.accountcqrseventsourcing.query.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.elaamiri.accountcqrseventsourcing.common_api.enumerations.AccountStatus;
import me.elaamiri.accountcqrseventsourcing.common_api.events.AccountCreatedEvent;
import me.elaamiri.accountcqrseventsourcing.common_api.events.AccountCreditedEvent;
import me.elaamiri.accountcqrseventsourcing.query.entities.Account;
import me.elaamiri.accountcqrseventsourcing.query.repositories.AccountRepository;
import me.elaamiri.accountcqrseventsourcing.query.repositories.OperationRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j // lombok logging
public class AccountServiceHandler {

    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    @EventHandler // @Event Sourcing handler is for Aggregate
    public void on(AccountCreatedEvent accountCreatedEvent){
        log.info("Event Received: **| AccountCreatedEvent |** ");
        Account account =  Account
                .builder()
                .id(accountCreatedEvent.getId())
                .balance(accountCreatedEvent.getInitialBalance())
                .accountStatus(accountCreatedEvent.getAccountStatus())
                .currency(accountCreatedEvent.getCurrency())
                .build();
        Account savedAccount = accountRepository.save(account);
        log.info(String.format("New Account Created [ID: %s]", savedAccount.getId()));
    }


}
