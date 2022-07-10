package de.capouschek.airqualitybackend.exceptions;

public class LoginFailedException extends Exception {
    public LoginFailedException(String message) {
        super(message);
    }
}
