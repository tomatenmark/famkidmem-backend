package de.mherrmann.famkidmem.backend.body.admin;

import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorized;

public class RequestBodyAddUser extends RequestBodyAuthorized {

    private String username;
    private String loginHash;
    private String passwordKeySalt;
    private String userKey;
    private String personId;
    private boolean admin;
    private boolean editor;

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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isEditor() {
        return editor;
    }

    public void setEditor(boolean editor) {
        this.editor = editor;
    }
}
