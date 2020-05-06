package de.mherrmann.famkidmem.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
public class PersonRelation {

    @Id
    private String id;

    @OneToOne
    @JoinColumn(name = "me_id", referencedColumnName = "id")
    private Person me;

    @OneToOne
    @JoinColumn(name = "other_id", referencedColumnName = "id")
    private Person other;

    @OneToOne
    @JoinColumn(name = "key_id", referencedColumnName = "id")
    private Key key;

    private String showAs;

    private PersonRelation(){}

    public PersonRelation(Person me, Person other, String showAs, Key key) {
        this.id = UUID.randomUUID().toString();
        this.me = me;
        this.other = other;
        this.showAs = showAs;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Person getMe() {
        return me;
    }

    public void setMe(Person me) {
        this.me = me;
    }

    public Person getOther() {
        return other;
    }

    public void setOther(Person other) {
        this.other = other;
    }

    public String getShowAs() {
        return showAs;
    }

    public void setShowAs(String showAs) {
        this.showAs = showAs;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
