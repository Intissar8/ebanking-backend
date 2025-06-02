package ma.emsi.ebankingbackend;

import ma.emsi.ebankingbackend.dtos.BankAccountDTO;
import ma.emsi.ebankingbackend.dtos.CurrentBankAccountDTO;
import ma.emsi.ebankingbackend.dtos.CustomerDTO;
import ma.emsi.ebankingbackend.dtos.SavingBankAccountDTO;
import ma.emsi.ebankingbackend.entities.*;
import ma.emsi.ebankingbackend.enums.AccountStatus;
import ma.emsi.ebankingbackend.enums.OperationType;
import ma.emsi.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.emsi.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.emsi.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.emsi.ebankingbackend.repositories.AccountOperationRepository;
import ma.emsi.ebankingbackend.repositories.BankAccountRepository;
import ma.emsi.ebankingbackend.repositories.CustomerRepository;
import ma.emsi.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService)
    {
        return args -> {
            Stream.of("Hassan","Yassine","Aicha").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                bankAccountService.saveCustomer(customer);
            });

            bankAccountService.ListCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());

                }catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }


            });

            List<BankAccountDTO> bankAccountsDTO = bankAccountService.bankAccountList();
            for(BankAccountDTO bankAccountDTO : bankAccountsDTO)
            {
                String accountId;
                if(bankAccountDTO instanceof SavingBankAccountDTO)
                {
                    accountId=((SavingBankAccountDTO)bankAccountDTO).getId();
                }else {

                    accountId=((CurrentBankAccountDTO)bankAccountDTO).getId();
                }
                for(int i=0;i<10;i++) {
                    bankAccountService.credit(accountId, 10000+Math.random()*12000,"Credit");
                    bankAccountService.debit(accountId, 10000+Math.random()*9000,"Dedit");
                }
            }





















           /* BankAccount bankAccount = bankAccountRepository.findById("03113f27-581f-453c-a0b9-0885a83eb5ba").orElse(null);
            if(bankAccount != null){
                System.out.println("**********************************");
                System.out.println(bankAccount.getId());
                System.out.println(bankAccount.getBalance());
                System.out.println(bankAccount.getCreatedAt());
                System.out.println(bankAccount.getStatus());
                System.out.println(bankAccount.getCustomer().getName());
                System.out.println(bankAccount.getClass().getSimpleName());//to print the name of the child class like savingaccount or currentaccount
                if(bankAccount instanceof CurrentAccount){
                    System.out.println("Overdraft="+((CurrentAccount)bankAccount).getOverdraft());

                }else if(bankAccount instanceof SavingAccount){

                    System.out.println("InterestRate="+((SavingAccount)bankAccount).getInterestRate());
                }
                System.out.println("**********************************");
                bankAccount.getAccountOperations().forEach(op-> {

                    System.out.println(op.getType()+"\t"+op.getAmount()+"\t"+op.getOperationDate());
                });}*/

        };
    }


    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository) {
        return args -> {
            Stream.of("Hassan","Yassine","Aicha").forEach(name -> {
                Customer customer = new Customer();
               customer.setName(name);
               customer.setEmail(name + "@gmail.com");
               customerRepository.save(customer);
            });

            customerRepository.findAll().forEach(cust -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverdraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);

            });

        bankAccountRepository.findAll().forEach(acc -> {
           for(int i=0;i<10;i++)
           {
               AccountOperation accountOperation = new AccountOperation();
               accountOperation.setOperationDate(new Date());
               accountOperation.setAmount(Math.random()*120000);
               accountOperation.setType(Math.random()>0.5? OperationType.DEBIT:OperationType.CREDIT );
               accountOperation.setBankAccount(acc);
               accountOperationRepository.save(accountOperation);


           }


        });


        };
    }

}
