package ma.emsi.ebankingbackend.services;

import ma.emsi.ebankingbackend.dtos.*;
import ma.emsi.ebankingbackend.entities.BankAccount;
import ma.emsi.ebankingbackend.entities.CurrentAccount;
import ma.emsi.ebankingbackend.entities.Customer;
import ma.emsi.ebankingbackend.entities.SavingAccount;
import ma.emsi.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.emsi.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.emsi.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {

    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)throws CustomerNotFoundException;
    List<CustomerDTO> ListCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount,String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount,String description)throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDest, double amount)throws BankAccountNotFoundException, BalanceNotSufficientException ;
    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);


    AccountHistoryDTO getAccountHistory(String accountId,int page,int size)throws BankAccountNotFoundException;

    List<CustomerDTO> searchCutomers(String keyword);

    List<Object> getAccountsByCustomerId(Long customerId);
}
