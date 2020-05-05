package de.mherrmann.famkidmem.backend.body.admin;

public class RequestBodyAddUser {

    private String username;
    private String loginHash;
    private String passwordKeySalt;
    private String userKey;
    private String personId;
    private String key;
    private String iv;

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

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
