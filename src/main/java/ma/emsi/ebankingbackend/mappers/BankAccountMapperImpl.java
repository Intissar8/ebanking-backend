package ma.emsi.ebankingbackend.mappers;

import ma.emsi.ebankingbackend.dtos.AccountOperationDTO;
import ma.emsi.ebankingbackend.dtos.CurrentBankAccountDTO;
import ma.emsi.ebankingbackend.dtos.CustomerDTO;
import ma.emsi.ebankingbackend.dtos.SavingBankAccountDTO;
import ma.emsi.ebankingbackend.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {
    public CustomerDTO fromCustomer(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);//we use the map to transfer data from an object to another
        return customerDTO;
    }

    public Customer fromCustomerDTO(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }

    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingBankAccount) {
           SavingBankAccountDTO savingBankAccountDTO = new SavingBankAccountDTO();
           BeanUtils.copyProperties(savingBankAccount, savingBankAccountDTO);
           savingBankAccountDTO.setCustomerDTO(fromCustomer(savingBankAccount.getCustomer()));//some time the BeanUtils might not load the client so we need to do it by mapping it to dto then set
           savingBankAccountDTO.setType(savingBankAccount.getClass().getSimpleName());
           return savingBankAccountDTO;


    }

    public SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO savingBankAccountDTO) {
         SavingAccount savingAccount = new SavingAccount();
         BeanUtils.copyProperties(savingBankAccountDTO, savingAccount);
         savingAccount.setCustomer(fromCustomerDTO(savingBankAccountDTO.getCustomerDTO()));
         return savingAccount;
    }

    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentBankAccount) {
        CurrentBankAccountDTO currentBankAccountDTO = new CurrentBankAccountDTO();
        BeanUtils.copyProperties(currentBankAccount, currentBankAccountDTO);
        currentBankAccountDTO.setCustomerDTO(fromCustomer(currentBankAccount.getCustomer()));//some time the BeanUtils might not load the client so we need to do it by mapping it to dto then set
        currentBankAccountDTO.setType(currentBankAccount.getClass().getSimpleName());
        return currentBankAccountDTO;



    }

    public CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO currentBankAccountDTO) {
        CurrentAccount currentAccount = new CurrentAccount();
        BeanUtils.copyProperties(currentBankAccountDTO, currentAccount);
        currentAccount.setCustomer(fromCustomerDTO(currentBankAccountDTO.getCustomerDTO()));
        return currentAccount;

    }

    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation) {
        AccountOperationDTO accountOperationDTO = new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation, accountOperationDTO);
        return accountOperationDTO;



    }
    public Object fromBankAccount(BankAccount bankAccount) {
        if (bankAccount instanceof SavingAccount) {
            return fromSavingBankAccount((SavingAccount) bankAccount);
        } else if (bankAccount instanceof CurrentAccount) {
            return fromCurrentBankAccount((CurrentAccount) bankAccount);
        } else {
            throw new IllegalArgumentException("Unknown bank account type: " + bankAccount.getClass().getName());
        }
    }

}
