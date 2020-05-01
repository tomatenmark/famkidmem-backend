package de.mherrmann.famkidmem.backend.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class UserEntity {
    @Id
    private String id;

    private String userName;
    private String displayName;
    private String loginHashHash;
    private String master_key;
    private String accessToken;
    private boolean admin;
    private boolean editor;
    private boolean init; //indicates the user has to change username and password after login
    private boolean reset; //indicates the user has to change password after login

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private List<UserSession> sessions = new ArrayList<>();

    public UserEntity(){}

    public UserEntity(String userName, String displayName, String loginHashHash, String master_key, boolean admin, boolean editor) {
        this.id = UUID.randomUUID().toString();
        this.userName = userName;
        this.displayName = displayName;
        this.loginHashHash = loginHashHash;
        this.master_key = master_key;
        this.admin = admin;
        this.editor = editor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLoginHashHash() {
        return loginHashHash;
    }

    public void setLoginHashHash(String loginHashHash) {
        this.loginHashHash = loginHashHash;
    }

    public String getMaster_key() {
        return master_key;
    }

    public void setMaster_key(String master_key) {
        this.master_key = master_key;
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
