package de.mherrmann.famkidmem.backend.exception;

public class PersonActionException extends Exception {

    public PersonActionException(String action, String reason){
        super(String.format("Could not %s person. Reason: %s", action, reason));
    }
}
