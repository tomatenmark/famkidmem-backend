package de.mherrmann.famkidmem.backend.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class UserSession {

    @Id
    private String id;

    private String accessToken;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    private Timestamp lastRequest;

    public UserSession(){}

    public UserSession(UserEntity user, String accessToken){
        this.id = UUID.randomUUID().toString();
        this.setUserEntity(user);
        this.setAccessToken(accessToken);
        this.setLastRequest(new Timestamp(System.currentTimeMillis()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Timestamp getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(Timestamp lastRequest) {
        this.lastRequest = lastRequest;
    }
}
