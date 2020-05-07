package de.mherrmann.famkidmem.backend.exception;

public class AddPersonException extends Exception {
    public AddPersonException(String reason){
        super("Could not add person. Reason: " + reason);
    }
}
