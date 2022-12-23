package me.elaamiri.accountcqrseventsourcing.common_api.exceptions;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(String message) {
        super(message);
    }
}
