package de.mherrmann.famkidmem.backend.body.authorized;

public class RequestBodyAuthorizedChangeValue extends RequestBodyAuthorized {

    private String newValue;

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
