package ma.emsi.ebankingbackend.web;

import lombok.AllArgsConstructor;
import ma.emsi.ebankingbackend.dtos.*;
import ma.emsi.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.emsi.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.emsi.ebankingbackend.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountRestController {
private BankAccountService bankAccountService;

@GetMapping("/accounts/{accountId}")
public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
return bankAccountService.getBankAccount(accountId);
}
@GetMapping("/accounts")
public List<BankAccountDTO> listAccounts()
{
    return bankAccountService.bankAccountList();
}

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId) {
    return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(@PathVariable String accountId, @RequestParam(name="page",defaultValue = "0") int page, @RequestParam(name="size",defaultValue ="5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size);

    }
    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException {
        this.bankAccountService.credit(creditDTO.getAccountId(),creditDTO.getAmount(),creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferRequestDTO transferRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.transfer(transferRequestDTO.getAccountSource(), transferRequestDTO.getAccountDestination(), transferRequestDTO.getAmount());
    }

    @GetMapping("/customers/{customerId}/accounts")
    public List<Object> getAccountsByCustomer(@PathVariable Long customerId) {
        return bankAccountService.getAccountsByCustomerId(customerId);
    }



}
