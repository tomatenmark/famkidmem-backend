package de.mherrmann.famkidmem.backend.body.authorized;

public class RequestBodyAuthorizedChangePassword extends RequestBodyAuthorized {

    private String newLoginHash;
    private String newUserKey;

    public String getNewLoginHash() {
        return newLoginHash;
    }

    public void setNewLoginHash(String newLoginHash) {
        this.newLoginHash = newLoginHash;
    }

    public String getNewUserKey() {
        return newUserKey;
    }

    public void setNewUserKey(String newUserKey) {
        this.newUserKey = newUserKey;
    }
}
