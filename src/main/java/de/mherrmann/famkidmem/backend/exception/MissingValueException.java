package de.mherrmann.famkidmem.backend.exception;

public class MissingValueException extends Exception {
    public MissingValueException(String missingValue){
        super("Missing value: " + missingValue);
    }
}
