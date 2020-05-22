package de.mherrmann.famkidmem.backend.body.edit;

import java.util.List;

public class RequestBodyUpdateVideo {

    private String designator;
    private String title;
    private String description;
    private boolean recordedInCologne;
    private boolean recordedInGardelgen;
    private List<Integer> years;
    private List<String> persons;
    private String key;
    private String iv;
    private String thumbnailKey;
    private String thumbnailIv;

    public String getDesignator() {
        return designator;
    }

    public void setDesignator(String designator) {
        this.designator = designator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRecordedInCologne() {
        return recordedInCologne;
    }

    public void setRecordedInCologne(boolean recordedInCologne) {
        this.recordedInCologne = recordedInCologne;
    }

    public boolean isRecordedInGardelgen() {
        return recordedInGardelgen;
    }

    public void setRecordedInGardelgen(boolean recordedInGardelgen) {
        this.recordedInGardelgen = recordedInGardelgen;
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public List<String> getPersons() {
        return persons;
    }

    public void setPersons(List<String> persons) {
        this.persons = persons;
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

    public String getThumbnailKey() {
        return thumbnailKey;
    }

    public void setThumbnailKey(String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
    }

    public String getThumbnailIv() {
        return thumbnailIv;
    }

    public void setThumbnailIv(String thumbnailIv) {
        this.thumbnailIv = thumbnailIv;
    }
}
