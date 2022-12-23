package me.elaamiri.accountcqrseventsourcing.common_api.dtos.query;
import me.elaamiri.accountcqrseventsourcing.common_api.enumerations.AccountStatus;
import me.elaamiri.accountcqrseventsourcing.query.entities.Operation;

import java.util.Collection;

public class AccountResponseDTO {
        private String id;
        private String currency;
        private double balance;
        private AccountStatus accountStatus;
}
