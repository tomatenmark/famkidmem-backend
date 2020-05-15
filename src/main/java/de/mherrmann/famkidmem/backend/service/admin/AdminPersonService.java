package de.mherrmann.famkidmem.backend.service.admin;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.body.admin.*;
import de.mherrmann.famkidmem.backend.entity.FileEntity;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.exception.PersonActionException;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.repository.FileRepository;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminPersonService {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final FileRepository fileRepository;
    private final KeyRepository keyRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminPersonService.class);

    @Autowired
    public AdminPersonService(PersonRepository personRepository, UserRepository userRepository, FileRepository fileRepository, KeyRepository keyRepository) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.keyRepository = keyRepository;
    }

    public void addPerson(RequestBodyAddPerson addPersonRequest) throws PersonActionException {
        doAddPersonChecks(addPersonRequest);
        Key key = addKey(addPersonRequest.getKey(), addPersonRequest.getIv());
        FileEntity face = addFace(addPersonRequest.getFaceFile(), addPersonRequest.getFaceKey(), addPersonRequest.getFaceIv());
        addPerson(addPersonRequest.getFirstName(), addPersonRequest.getLastName(), addPersonRequest.getCommonName(), face, key);
    }

    public void updatePerson(RequestBodyUpdatePerson updatePersonRequest) throws EntityNotFoundException, PersonActionException {
        Person oldPerson = getPerson(updatePersonRequest);
        doUpdatePersonChecks(updatePersonRequest);
        Key faceKey = oldPerson.getFace().getKey();
        if(!faceKey.getIv().equals(updatePersonRequest.getFaceIv())){
            faceKey = updateFaceKey(updatePersonRequest.getFaceKey(), updatePersonRequest.getFaceIv(), faceKey);
        }
        updatePerson(oldPerson, updatePersonRequest, faceKey);
    }

    public void deletePerson(RequestBodyDeletePerson deletePersonRequest) throws EntityNotFoundException, PersonActionException {
        Person person = getPerson(deletePersonRequest);
        checkForUser(person);
        delete(person);
    }

    public ResponseBodyGetPersons getPersons(){
        List<Person> persons = new ArrayList<>();
        Iterable<Person> personIterable = personRepository.findAll();
        personIterable.forEach(persons::add);
        ResponseBodyGetPersons usersResponse = new ResponseBodyGetPersons(persons);
        LOGGER.info("Successfully get persons");
        return usersResponse;
    }

    Person getPerson(String firstName, String lastName, String commonName) throws EntityNotFoundException {
        Optional<Person> personOptional =
                personRepository.findByFirstNameAndLastNameAndCommonName(firstName, lastName, commonName);
        if(!personOptional.isPresent()){
            LOGGER.error("Could not get person. Invalid peron names. {}, {}, {}", firstName, lastName, commonName);
            throw new EntityNotFoundException(Person.class, String.format("%s, %s, %s", firstName, lastName, commonName));
        }
        return personOptional.get();
    }

    private void checkForUser(Person person) throws PersonActionException {
        Optional<UserEntity> userOptional = userRepository.findByPerson(person);
        if(userOptional.isPresent()){
            String username = userOptional.get().getUsername();
            LOGGER.error("Could not delete person. Person still has an user. Username: {}", username);
            throw new PersonActionException("delete", "This person still has an user. Username: " + username);
        }
    }

    private void delete(Person person){
        personRepository.delete(person);
        fileRepository.delete(person.getFace());
        keyRepository.delete(person.getFace().getKey());
        keyRepository.delete(person.getKey());
    }

    private Person getPerson(RequestBodyUpdatePerson updatePersonRequest) throws EntityNotFoundException {
        String firstName = updatePersonRequest.getOldFirstName();
        String lastName = updatePersonRequest.getOldLastName();
        String commonName = updatePersonRequest.getOldCommonName();
        return getPerson(firstName, lastName, commonName);
    }

    private Person getPerson(RequestBodyDeletePerson deletePersonRequest) throws EntityNotFoundException {
        String firstName = deletePersonRequest.getFirstName();
        String lastName = deletePersonRequest.getLastName();
        String commonName = deletePersonRequest.getCommonName();
        return getPerson(firstName, lastName, commonName);
    }

    private void doAddPersonChecks(RequestBodyAddPerson addPersonRequest) throws PersonActionException {
        String firstName = addPersonRequest.getFirstName();
        String lastName = addPersonRequest.getLastName();
        String commonName = addPersonRequest.getCommonName();
        if(personRepository.existsByFirstNameAndLastNameAndCommonName(firstName, lastName, commonName)){
            LOGGER.error("Could not add Person. Person with same names already exists. {}, {}, {}", firstName, lastName, commonName);
            throw new PersonActionException("add", "Person with same names already exists.");
        }
        File file = new File(Application.filesDir + addPersonRequest.getFaceFile());
        if(!file.exists() || file.isDirectory()){
            LOGGER.error("Could not add Person. File {} does not exist or is an directory", addPersonRequest.getFaceFile());
            throw new PersonActionException("add", "Face file does not exist: " + addPersonRequest.getFaceFile());
        }
    }

    private void doUpdatePersonChecks(RequestBodyUpdatePerson updatePersonRequest) throws PersonActionException {
        String firstName = updatePersonRequest.getFirstName();
        String lastName = updatePersonRequest.getLastName();
        String commonName = updatePersonRequest.getCommonName();
        Optional<Person> personOptional = personRepository.findByFirstNameAndLastNameAndCommonName(firstName, lastName, commonName);
        if(personOptional.isPresent() && !sameNames(updatePersonRequest)){
            LOGGER.error("Could not update Person. Another person with same names already exists. {}, {}, {}", firstName, lastName, commonName);
            throw new PersonActionException("update", "Another person with same names already exists.");
        }
    }

    private boolean sameNames(RequestBodyUpdatePerson updatePersonRequest){
        String firstName = updatePersonRequest.getFirstName();
        String lastName = updatePersonRequest.getLastName();
        String commonName = updatePersonRequest.getCommonName();
        String oldFirstName = updatePersonRequest.getOldFirstName();
        String oldLastName = updatePersonRequest.getOldLastName();
        String oldCommonName = updatePersonRequest.getOldCommonName();
        return firstName.equals(oldFirstName) && lastName.equals(oldLastName) && commonName.equals(oldCommonName);
    }

    private void addPerson(String firstName, String lastName, String commonName, FileEntity face, Key key){
        Person person = new Person(firstName, lastName, commonName, face, key);
        personRepository.save(person);
    }

    private void updatePerson(Person oldPerson, RequestBodyUpdatePerson updatePersonRequest, Key faceKey){
        Person person = new Person(updatePersonRequest.getFirstName(), updatePersonRequest.getLastName(),
                updatePersonRequest.getCommonName(), oldPerson.getFace(), oldPerson.getKey());
        person.getFace().setKey(faceKey);
        person.setId(oldPerson.getId());
        personRepository.save(person);
    }

    private FileEntity addFace(String filename, String key, String iv){
        Key keyEntity = addKey(key, iv);
        FileEntity file = new FileEntity(keyEntity, filename);
        return fileRepository.save(file);
    }

    private Key addKey(String key, String iv){
        Key keyEntity = new Key(key, iv);
        return keyRepository.save(keyEntity);
    }

    private Key updateFaceKey(String key, String iv, Key oldKey){
        Key keyEntity = new Key(key, iv);
        keyEntity.setId(oldKey.getId());
        return keyRepository.save(keyEntity);
    }
}
