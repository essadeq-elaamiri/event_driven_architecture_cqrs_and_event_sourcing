package me.elaamiri.accountcqrseventsourcing.query.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.elaamiri.accountcqrseventsourcing.common_api.enumerations.AccountStatus;
import me.elaamiri.accountcqrseventsourcing.common_api.enumerations.OperationType;
import me.elaamiri.accountcqrseventsourcing.common_api.events.AccountActivatedEvent;
import me.elaamiri.accountcqrseventsourcing.common_api.events.AccountCreatedEvent;
import me.elaamiri.accountcqrseventsourcing.common_api.events.AccountCreditedEvent;
import me.elaamiri.accountcqrseventsourcing.common_api.events.AccountDebitedEvent;
import me.elaamiri.accountcqrseventsourcing.common_api.exceptions.AccountNotFoundException;
import me.elaamiri.accountcqrseventsourcing.query.entities.Account;
import me.elaamiri.accountcqrseventsourcing.query.entities.Operation;
import me.elaamiri.accountcqrseventsourcing.query.repositories.AccountRepository;
import me.elaamiri.accountcqrseventsourcing.query.repositories.OperationRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j // lombok logging
@Transactional
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

    @EventHandler
    public void on(AccountActivatedEvent accountActivatedEvent){
        log.info("Event Received: **| AccountActivatedEvent |** ");
        Account account = accountRepository.findById(accountActivatedEvent.getId()).get();
        account.setAccountStatus(accountActivatedEvent.getAccountStatus());
        Account savedAccount = accountRepository.save(account);
        log.info(String.format("New Account Updated [ID: %s]", savedAccount.getId()));
    }

    @EventHandler
    public void on(AccountCreditedEvent accountCreditedEvent){
        log.info("Event Received: **| AccountCreditedEvent |** ");
        Account account = accountRepository.findById(accountCreditedEvent.getId())
                .orElseThrow(()->{
                    throw new AccountNotFoundException("Account with this ID is NOT Found.");
                });
        account.setBalance(account.getBalance() + accountCreditedEvent.getAmount());

        // update account
        Account savedAccount = accountRepository.save(account);
        log.info(String.format("New Account Updated [ID: %s]", savedAccount.getId()));

        Operation operation = Operation.builder()
                .amount(accountCreditedEvent.getAmount())
                .date(new Date())
                .type(OperationType.CREDIT)
                .account(savedAccount)
                .build();
        operationRepository.save(operation);
        log.info("New  CREDIT Operation Created.");

    }

    @EventHandler
    public void on(AccountDebitedEvent accountDebitedEvent){
        log.info("Event Received: **| AccountDebitedEvent |** ");
        Account account = accountRepository.findById(accountDebitedEvent.getId())
                .orElseThrow(()->{
                    throw new AccountNotFoundException("Account with this ID is NOT Found.");
                });

        account.setBalance(account.getBalance() - accountDebitedEvent.getAmount());

        // update account
        Account savedAccount = accountRepository.save(account);
        log.info(String.format("New Account Updated [ID: %s]", savedAccount.getId()));

        Operation operation = Operation.builder()
                .amount(accountDebitedEvent.getAmount())
                .date(new Date())
                .type(OperationType.DEBIT)
                .account(savedAccount)
                .build();
        operationRepository.save(operation);
        log.info("New  DEBIT Operation Created.");
    }


}
