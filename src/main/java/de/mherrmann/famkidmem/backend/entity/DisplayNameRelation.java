package de.mherrmann.famkidmem.backend.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class DisplayNameRelation {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "me_id", referencedColumnName = "id")
    private UserEntity me;

    @ManyToOne
    @JoinColumn(name = "other_id", referencedColumnName = "id")
    private UserEntity other;

    private String name;

    private DisplayNameRelation(){}

    public DisplayNameRelation(UserEntity me, UserEntity other, String name) {
        this.id = UUID.randomUUID().toString();
        this.me = me;
        this.other = other;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEntity getMe() {
        return me;
    }

    public void setMe(UserEntity me) {
        this.me = me;
    }

    public UserEntity getOther() {
        return other;
    }

    public void setOther(UserEntity other) {
        this.other = other;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
