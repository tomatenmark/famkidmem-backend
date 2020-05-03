package de.mherrmann.famkidmem.backend.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String username){
        super("User does not exist: " + username);
    }
}
