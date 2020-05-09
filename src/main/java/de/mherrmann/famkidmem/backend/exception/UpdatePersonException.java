package de.mherrmann.famkidmem.backend.exception;

public class UpdatePersonException extends Exception {
    public UpdatePersonException(String reason){
        super("Could not update person. Reason: " + reason);
    }
}
