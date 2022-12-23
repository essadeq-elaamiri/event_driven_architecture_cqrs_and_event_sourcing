package me.elaamiri.accountcqrseventsourcing.common_api.events;

import lombok.Getter;

public class AccountDebitedEvent extends BaseEvent<String>{
    @Getter
    private double amount;
    @Getter
    private String currency;

    public AccountDebitedEvent(String s, double amount, String currency) {
        super(s);
        this.amount = amount;
        this.currency = currency;
    }
}
