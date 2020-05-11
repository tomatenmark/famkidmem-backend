package de.mherrmann.famkidmem.backend.body.admin;

public class RequestBodyUpdatePerson {
    private String oldFirstName;
    private String oldLastName;
    private String oldCommonName;
    private String firstName;
    private String lastName;
    private String commonName;
    private String faceKey;
    private String faceIv;

    public String getOldFirstName() {
        return oldFirstName;
    }

    public void setOldFirstName(String oldFirstName) {
        this.oldFirstName = oldFirstName;
    }

    public String getOldLastName() {
        return oldLastName;
    }

    public void setOldLastName(String oldLastName) {
        this.oldLastName = oldLastName;
    }

    public String getOldCommonName() {
        return oldCommonName;
    }

    public void setOldCommonName(String oldCommonName) {
        this.oldCommonName = oldCommonName;
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
