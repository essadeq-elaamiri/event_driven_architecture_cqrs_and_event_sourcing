package me.elaamiri.accountcqrseventsourcing.query.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.elaamiri.accountcqrseventsourcing.query.entities.Account;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/query/accounts")
@Slf4j
@AllArgsConstructor
public class AccountQueryController {
    private QueryGateway queryGateway;


    @GetMapping("/")
    public List<Account> getAllAccounts(){
        queryGateway.query(new GET)
    }
}
