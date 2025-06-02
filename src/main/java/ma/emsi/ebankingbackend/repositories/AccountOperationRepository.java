package ma.emsi.ebankingbackend.repositories;

import ma.emsi.ebankingbackend.entities.AccountOperation;
import ma.emsi.ebankingbackend.entities.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation,Long> {
    List<AccountOperation> findByBankAccountId(String id);
    Page<AccountOperation> findByBankAccountIdOrderByOperationDateDesc(String id, Pageable pageable);//This is the same as the first one except this is with pagination
}
