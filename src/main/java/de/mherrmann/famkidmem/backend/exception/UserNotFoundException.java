package de.mherrmann.famkidmem.backend.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String username){
        super("User not found. Username: " + username);
    }
}
