package de.mherrmann.famkidmem.backend.body.admin;


import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.UserEntity;

import java.util.List;

public class ResponseBodyGetUsers extends ResponseBody {

    private List<UserEntity> users;
    private Key personKey;
    private String userKey;

    public ResponseBodyGetUsers(){}

    public ResponseBodyGetUsers(List<UserEntity> users, Key personKey, String userKey){
        super("ok", "Successfully get users");
        this.setUsers(users);
        this.setPersonKey(personKey);
        this.setUserKey(userKey);
    }

    public ResponseBodyGetUsers(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public Key getPersonKey() {
        return personKey;
    }

    public void setPersonKey(Key personKey) {
        this.personKey = personKey;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
