package de.mherrmann.famkidmem.backend.body.admin;


import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.UserEntity;

import java.util.List;

public class ResponseBodyGetPersons extends ResponseBody {

    private List<Person> persons;

    public ResponseBodyGetPersons(){}

    public ResponseBodyGetPersons(List<Person> persons){
        super("ok", "Successfully get persons");
        this.setPersons(persons);
    }

    public ResponseBodyGetPersons(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
