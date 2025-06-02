package ma.emsi.ebankingbackend.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.ebankingbackend.dtos.AccountOperationDTO;
import ma.emsi.ebankingbackend.dtos.CustomerDTO;
import ma.emsi.ebankingbackend.entities.Customer;
import ma.emsi.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.emsi.ebankingbackend.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")//to say that other applications from other domains have acces to this data
public class CustomerRestController {
    private BankAccountService bankAccountService;//the controller communicate only with couche BankAccountService

    @GetMapping("/customers")
    public List<CustomerDTO> customers()
    {
        return bankAccountService.ListCustomers();

    }

    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword",defaultValue = "") String keyword)
    {
        return bankAccountService.searchCutomers("%"+keyword+"%");

    }

    @GetMapping("/customers/{id}")//we use get if we want to print data
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerId);

    }

    @PostMapping("/customers")//we use post if we want to save data // RequestBody is used pour que les donnees du Customer previent du cors de la requete
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) {
       return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{id}")//in Restfull we need to use Put for updates
    public CustomerDTO updateCustomer(@RequestBody CustomerDTO customerDTO,@PathVariable(name = "id") Long customerId)//for PathVariable if the name in the url is the same as the attribute than there is no need to use (name = "id")
    {
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId){
         bankAccountService.deleteCustomer(customerId);

    }


}
