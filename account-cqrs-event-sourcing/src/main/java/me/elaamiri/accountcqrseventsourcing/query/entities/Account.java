package me.elaamiri.accountcqrseventsourcing.query.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.elaamiri.accountcqrseventsourcing.common_api.enumerations.AccountStatus;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Account {
    @Id
    private String id;
    private String currency;
    private double balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @OneToMany(mappedBy = "account")
    private Collection<Operation> operations;
}
