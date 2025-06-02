package ma.emsi.ebankingbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.ebankingbackend.dtos.*;
import ma.emsi.ebankingbackend.entities.*;
import ma.emsi.ebankingbackend.enums.OperationType;
import ma.emsi.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.emsi.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.emsi.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.emsi.ebankingbackend.mappers.BankAccountMapperImpl;
import ma.emsi.ebankingbackend.repositories.AccountOperationRepository;
import ma.emsi.ebankingbackend.repositories.BankAccountRepository;
import ma.emsi.ebankingbackend.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {


    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;
   // private  final Logger log = LoggerFactory.getLogger(this.getClass().getName());//we use it for logs or journalier we can use annotation @Slf4j

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        CurrentAccount currentAccount;
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if(customer==null) {
            throw new CustomerNotFoundException("Customer not found");


        }

        currentAccount=new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverdraft(overDraft);
        currentAccount.setCustomer(customer);
        CurrentAccount savedBankAccount=  bankAccountRepository.save(currentAccount);

        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        SavingAccount savingAccount;
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if(customer==null) {
            throw new CustomerNotFoundException("Customer not found");


        }

        savingAccount=new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        SavingAccount savedBankAccount=  bankAccountRepository.save(savingAccount);

        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }


    @Override
    public List<CustomerDTO> ListCustomers() {
        List<Customer> customers=customerRepository.findAll();

        return customers.stream().map(cust->dtoMapper.fromCustomer(cust)).collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException{
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount=(SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }else{
            CurrentAccount currentAccount=(CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId) //no need to use dto here because we don't need to display the data meaning there is no interaction with the ui
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
          if(bankAccount.getBalance()<amount) {
              throw new BalanceNotSufficientException("Balance not sufficient");
          }
        AccountOperation accountOperation=new AccountOperation();
          accountOperation.setType(OperationType.DEBIT);
          accountOperation.setAmount(amount);
          accountOperation.setDescription(description);
          accountOperation.setOperationDate(new Date());
          accountOperation.setBankAccount(bankAccount);
          accountOperationRepository.save(accountOperation);
          bankAccount.setBalance(bankAccount.getBalance()-amount);
          bankAccountRepository.save(bankAccount);

    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException{
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDest, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
      debit(accountIdSource,amount,"transfer to "+accountIdDest);
      credit(accountIdDest,amount,"transfer from "+accountIdSource);

    }

    @Override
    public List<BankAccountDTO> bankAccountList()
    {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
      List<BankAccountDTO> bankAccountDTOS =  bankAccounts.stream().map(bankAccount -> {
            if(bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount=(SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            }else  {
                CurrentAccount currentAccount=(CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());

      return bankAccountDTOS;

    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
      Customer customer=  customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("Customer not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("updating customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId)  {
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId)  {
       List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op-> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId,int page,int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null) throw new BankAccountNotFoundException("Account not found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationsDTOS = accountOperations.stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationsDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCutomers(String keyword) {
        List<Customer> customers = customerRepository.searchCustomer(keyword);
        List<CustomerDTO> customerDTO = customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
         return customerDTO;
    }

    @Override
    public List<Object> getAccountsByCustomerId(Long customerId) {
        return bankAccountRepository.findByCustomerId(customerId).stream().map(dtoMapper::fromBankAccount).collect(Collectors.toList());
    }
}
