package de.pcmr.shop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception is thrown if a customer already exists to the given email when creating a new one.
 *
 * @author Fynn Lohse
 */

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = CustomerAlreadyExistsException.ERROR_MESSAGE)
public class CustomerAlreadyExistsException extends Exception {
    public static final String ERROR_MESSAGE = "Es existiert bereits ein Nutzer mit dieser Email";

    public CustomerAlreadyExistsException() {
        super(ERROR_MESSAGE);
    }
}
