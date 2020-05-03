package de.mherrmann.famkidmem.backend.body.admin;

import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorized;

public class RequestBodyResetPassword extends RequestBodyAuthorized {

    private String username;
    private String loginHash;
    private String passwordKeySalt;
    private String userKey;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginHash() {
        return loginHash;
    }

    public void setLoginHash(String loginHash) {
        this.loginHash = loginHash;
    }

    public String getPasswordKeySalt() {
        return passwordKeySalt;
    }

    public void setPasswordKeySalt(String passwordKeySalt) {
        this.passwordKeySalt = passwordKeySalt;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
