package de.mherrmann.famkidmem.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Picture {

    @Id
    private String id;

    private String key;
    private String iv;
    private String filename;

    private Picture(){}

    public Picture(String key, String iv, String filename) {
        this.id = UUID.randomUUID().toString();
        this.key = key;
        this.iv = iv;
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
