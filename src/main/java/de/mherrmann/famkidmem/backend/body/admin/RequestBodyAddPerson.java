package de.mherrmann.famkidmem.backend.body.admin;

public class RequestBodyAddPerson {

    private String firstName;
    private String lastName;
    private String commonName;
    private String pictureKey;
    private String pictureIv;
    private String pictureFilename;

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

    public String getPictureKey() {
        return pictureKey;
    }

    public void setPictureKey(String pictureKey) {
        this.pictureKey = pictureKey;
    }

    public String getPictureIv() {
        return pictureIv;
    }

    public void setPictureIv(String pictureIv) {
        this.pictureIv = pictureIv;
    }

    public String getPictureFilename() {
        return pictureFilename;
    }

    public void setPictureFilename(String pictureFilename) {
        this.pictureFilename = pictureFilename;
    }
}
