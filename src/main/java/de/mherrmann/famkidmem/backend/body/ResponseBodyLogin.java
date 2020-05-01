package de.mherrmann.famkidmem.backend.body;

public class ResponseBodyLogin extends ResponseBody {

    private String accessToken;

    private ResponseBodyLogin(){}

    public ResponseBodyLogin(String accessToken){
        super("ok", "Login was successful");
        this.accessToken = accessToken;
    }

    public ResponseBodyLogin(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
