package de.mherrmann.famkidmem.backend.entity;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class UserEntity {
    @Id
    private String id;

    private String username;
    private String passwordKeySalt;
    private String loginHashHash;
    private String userKey;
    private String accessToken;
    private boolean admin;
    private boolean editor;
    private boolean init; //indicates the user has to change username and password after login
    private boolean reset; //indicates the user has to change password after login

    @OneToMany(mappedBy = "userEntity")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<UserSession> sessions = new ArrayList<>();

    public UserEntity(){}

    public UserEntity(String username, String passwordKeySalt, String loginHashHash, String userKey, boolean admin, boolean editor) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.passwordKeySalt = passwordKeySalt;
        this.loginHashHash = loginHashHash;
        this.userKey = userKey;
        this.admin = admin;
        this.editor = editor;
    }

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

    public String getPasswordKeySalt() {
        return passwordKeySalt;
    }

    public void setPasswordKeySalt(String passwordKeySalt) {
        this.passwordKeySalt = passwordKeySalt;
    }

    public String getLoginHashHash() {
        return loginHashHash;
    }

    public void setLoginHashHash(String loginHashHash) {
        this.loginHashHash = loginHashHash;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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

    public List<UserSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<UserSession> sessions) {
        this.sessions = sessions;
    }


}
