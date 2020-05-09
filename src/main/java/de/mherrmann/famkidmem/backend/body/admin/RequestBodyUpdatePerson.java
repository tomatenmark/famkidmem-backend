package de.mherrmann.famkidmem.backend.body.admin;

public class RequestBodyUpdatePerson {
    private String id;
    private String firstName;
    private String lastName;
    private String commonName;
    private String faceKey;
    private String faceIv;

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

    public String getFaceKey() {
        return faceKey;
    }

    public void setFaceKey(String faceKey) {
        this.faceKey = faceKey;
    }

    public String getFaceIv() {
        return faceIv;
    }

    public void setFaceIv(String faceIv) {
        this.faceIv = faceIv;
    }
}
