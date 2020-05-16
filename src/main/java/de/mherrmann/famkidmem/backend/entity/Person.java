package de.mherrmann.famkidmem.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.UUID;

@Entity
@IdClass(PersonId.class)
public class Person {

    @Id
    private String id;

    @Id
    private String videoId;

    private String name;

    private Person() {}

    public Person(String name, String videoId) {
        this.id = UUID.randomUUID().toString();
        this.videoId = videoId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class PersonId implements Serializable {
    private static final long serialVersionUID = 791874012704232L;

    private String id;
    private String videoId;
}