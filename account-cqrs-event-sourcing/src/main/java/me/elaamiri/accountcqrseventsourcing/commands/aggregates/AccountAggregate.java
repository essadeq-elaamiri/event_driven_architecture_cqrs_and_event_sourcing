package me.elaamiri.accountcqrseventsourcing.commands.aggregates;

import lombok.NoArgsConstructor;
import me.elaamiri.accountcqrseventsourcing.common_api.commands.CreateAccountCommand;
import me.elaamiri.accountcqrseventsourcing.common_api.enumerations.AccountStatus;
import me.elaamiri.accountcqrseventsourcing.common_api.events.AccountActivatedEvent;
import me.elaamiri.accountcqrseventsourcing.common_api.events.AccountCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
//@NoArgsConstructor // Important
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId; // identifies the aggregation
    // This id will be mapped to the TargetAggregateIdentifier in the baseCommand
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate(){
        // Required by Axon
    }

    @CommandHandler // Subscribe to Command Bus, and listen to the CreateAccountCommand events
    public AccountAggregate(CreateAccountCommand createAccountCommand){
        // Business logic

        if(createAccountCommand.getInitialBalance() < 0) throw new RuntimeException("Invalid Initial Balance | Negative");
        AggregateLifecycle.apply(new AccountCreatedEvent(
                // Command to event
                createAccountCommand.getId(),
                createAccountCommand.getInitialBalance(),
                createAccountCommand.getCurrency()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent accountCreatedEvent){

        // The Aggregate is the Object Status
        this.accountId = accountCreatedEvent.getId();
        this.balance = accountCreatedEvent.getInitialBalance();
        this.currency = accountCreatedEvent.getCurrency();

        this.status = AccountStatus.CREATED;
        AggregateLifecycle.apply(new AccountActivatedEvent(accountCreatedEvent.getId(), AccountStatus.ACTIVATED));
    }

    // create an on() function for our event

    @EventSourcingHandler // Changing the aggregate status
    public void on(AccountActivatedEvent accountActivatedEvent){
        this.status = accountActivatedEvent.getAccountStatus();
    }

}
