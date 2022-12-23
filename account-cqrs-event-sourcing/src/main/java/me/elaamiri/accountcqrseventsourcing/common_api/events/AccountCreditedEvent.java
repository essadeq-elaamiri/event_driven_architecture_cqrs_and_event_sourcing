package me.elaamiri.accountcqrseventsourcing.common_api.events;

import lombok.Getter;

public class AccountCreditedEvent extends BaseEvent<String>{

    @Getter
    private double amount;
    @Getter
    private double currency;

    public AccountCreditedEvent(String s, double amount, double currency) {
        super(s);
        this.amount = amount;
        this.currency = currency;
    }
}
