package de.mherrmann.famkidmem.backend.body.admin;

import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorized;

public class RequestBodyDeleteUser extends RequestBodyAuthorized {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
