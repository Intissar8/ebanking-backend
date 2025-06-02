package ma.emsi.ebankingbackend.exceptions;

public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
// extends RuntimeException with this the exception is unservriller donc we don't have to add throws nameofexception next to the name of the method we want to use it with