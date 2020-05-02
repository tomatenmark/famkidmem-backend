package de.mherrmann.famkidmem.backend.body.authorized;

public class RequestBodyAuthorizedChangePassword extends RequestBodyAuthorized {

    private String newLoginHash;
    private String newPasswordKeySalt;
    private String newUserKey;

    public String getNewLoginHash() {
        return newLoginHash;
    }

    public void setNewLoginHash(String newLoginHash) {
        this.newLoginHash = newLoginHash;
    }

    public String getNewPasswordKeySalt() {
        return newPasswordKeySalt;
    }

    public void setNewPasswordKeySalt(String newPasswordKeySalt) {
        this.newPasswordKeySalt = newPasswordKeySalt;
    }

    public String getNewUserKey() {
        return newUserKey;
    }

    public void setNewUserKey(String newUserKey) {
        this.newUserKey = newUserKey;
    }
}
