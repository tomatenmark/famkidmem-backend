package de.mherrmann.famkidmem.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class UserEntity {
    @Id
    private String id;

    private String username;
    private String passwordKeySalt;
    private String loginHashHash;
    private String masterKey;

    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    private boolean init; //indicates the user has to change username and password after login
    private boolean reset; //indicates the user has to change password after login

    private UserEntity(){}

    public UserEntity(String username, String passwordKeySalt, String loginHashHash, String masterKey, Person person) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.passwordKeySalt = passwordKeySalt;
        this.loginHashHash = loginHashHash;
        this.masterKey = masterKey;
        this.person = person;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPasswordKeySalt() {
        return passwordKeySalt;
    }

    public void setPasswordKeySalt(String passwordKeySalt) {
        this.passwordKeySalt = passwordKeySalt;
    }

    @JsonIgnore
    public String getLoginHashHash() {
        return loginHashHash;
    }

    public void setLoginHashHash(String loginHashHash) {
        this.loginHashHash = loginHashHash;
    }

    @JsonIgnore
    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }
}
