package ma.emsi.ebankingbackend.dtos;

import lombok.Data;
 //we use the dto and mappers to avoid the problem with manytoone and we can modify this part without needing to miss with the entities
@Data
public class CustomerDTO {

    private Long id;
    private String name ;
    private String email ;

}
