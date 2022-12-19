package me.elaamiri.accountcqrseventsourcing.common_api.commands;

public class DebitAccountCommand extends BaseCommand<String>{

    private double amount;
    private String currency;

    public DebitAccountCommand(String id, double amount, String currency) {
        super(id);
        this.amount = amount;
        this.currency = currency;
    }
}

