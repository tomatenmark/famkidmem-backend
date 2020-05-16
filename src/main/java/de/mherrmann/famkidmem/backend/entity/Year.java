package de.mherrmann.famkidmem.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Year {

    @Id
    private String id;

    private int value;

    private Year() {}

    public Year(int value) {
        this.id = UUID.randomUUID().toString();
        this.value = value;
    }

    public String getId() {
        return id;
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
