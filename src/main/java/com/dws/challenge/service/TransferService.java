package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InvalidAccountIdException;
import com.dws.challenge.exception.InvalidAmountException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferService {

    @Getter
    private final AccountsService accountsService;
    @Getter
    private final NotificationService notificationService;

    @Autowired
    public TransferService(AccountsService accountsService, NotificationService notificationService) {
        this.accountsService = accountsService;
        this.notificationService = notificationService;
    }


    public void transferBetweenAccounts(String accountFromId, String accountToId, BigDecimal amount){
        if(amount == null || amount.compareTo(BigDecimal.ZERO) < 0){
        throw new InvalidAmountException("Amount should be positive");
        }
        Account accountFrom = accountsService.getAccount(accountFromId);
        if(accountFrom == null){
           throw new InvalidAccountIdException("The account id " + accountFromId + " doesn't exist");
        }

        Account accountTo = accountsService.getAccount(accountToId);
        if(accountTo == null){
            throw new InvalidAccountIdException("The account id " + accountToId + " doesn't exist");
        }

        if (accountFrom.hashCode() < accountTo.hashCode()) {
            synchronized (accountFrom) {
                synchronized (accountTo) {
                    if (accountFrom.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
                        throw new InvalidAmountException("The amount exceeds the available balance");
                    }

                    accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
                    accountTo.setBalance(accountTo.getBalance().add(amount));
                }
            }
        } else{
            synchronized (accountTo) {
                synchronized (accountFrom) {
                    if (accountFrom.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
                        throw new InvalidAmountException("The amount exceeds the available balance");
                    }

                    accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
                    accountTo.setBalance(accountTo.getBalance().add(amount));
                }
            }
        }

        notificationService.notifyAboutTransfer(accountFrom, amount.toString());
        notificationService.notifyAboutTransfer(accountTo, amount.toString());

    }
}
