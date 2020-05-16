package de.mherrmann.famkidmem.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.UUID;

@Entity
@IdClass(YearId.class)
public class Year {

    @Id
    private String id;

    @Id
    private String videoId;

    private int value;

    private Year() {}

    public Year(int value, String videoId) {
        this.id = UUID.randomUUID().toString();
        this.videoId = videoId;
        this.value = value;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

class YearId implements Serializable {
    private static final long serialVersionUID = -12374972394729L;

    private String id;
    private String videoId;
}