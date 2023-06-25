package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InvalidAmountException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


@ExtendWith(SpringExtension.class)
@SpringBootTest


public class TransferServiceTest {

    @Autowired
    private TransferService transferService;
    @Autowired
    private AccountsService accountsService;

    @Test
    void transferBalance() {
        Account accountFrom = createAccount("id-123", new BigDecimal(500));
        Account accountTo = createAccount("id-125", new BigDecimal(300));

        this.transferService.transferBetweenAccounts(accountFrom.getAccountId(), accountTo.getAccountId(), new BigDecimal(200));
        assertThat(this.accountsService.getAccount("id-125").getBalance().equals(new BigDecimal(500)));
    }

    @Test
    void doesntAllowOverdraft() {
        Account accountFrom = createAccount("id-123", new BigDecimal(500));
        Account accountTo = createAccount("id-125", new BigDecimal(300));

        try {
            this.transferService.transferBetweenAccounts(accountFrom.getAccountId(), accountTo.getAccountId(), new BigDecimal(600));
            fail("Should have failed when trying to transfer more than the available balance");
        } catch (InvalidAmountException ex) {
            assertThat(ex.getMessage()).isEqualTo("The amount exceeds the available balance");
        }

    }

    private Account createAccount(String id, BigDecimal balance) {
        Account account = new Account(id);
        account.setBalance(balance);
        this.accountsService.createAccount(account);
        return account;
    }
}
