package de.mherrmann.famkidmem.backend.body;


public class RequestBodyLogin {

    private String userName;
    private String loginHash;

    public RequestBodyLogin(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginHash() {
        return loginHash;
    }

    public void setLoginHash(String loginHash) {
        this.loginHash = loginHash;
    }
}
