package de.mherrmann.famkidmem.backend.body;


public class RequestBodyLogin {

    private String username;
    private String loginHash;

    public RequestBodyLogin(){}

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
}
