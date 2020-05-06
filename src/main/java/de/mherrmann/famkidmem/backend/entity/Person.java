package de.mherrmann.famkidmem.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
public class Person {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String commonName;

    @OneToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private FileEntity face;

    @OneToOne
    @JoinColumn(name = "key_id", referencedColumnName = "id")
    private Key key;

    private Person(){}

    public Person(String firstName, String lastName, String commonName, FileEntity face, Key key) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.commonName = commonName;
        this.face = face;
        this.key = key;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public FileEntity getFace() {
        return face;
    }

    public void setFace(FileEntity face) {
        this.face = face;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
