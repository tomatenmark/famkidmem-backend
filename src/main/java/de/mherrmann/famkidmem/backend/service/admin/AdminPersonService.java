package de.mherrmann.famkidmem.backend.service.admin;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddPerson;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyUpdatePerson;
import de.mherrmann.famkidmem.backend.entity.FileEntity;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.exception.AddPersonException;
import de.mherrmann.famkidmem.backend.exception.AddUserException;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.exception.UpdatePersonException;
import de.mherrmann.famkidmem.backend.repository.FileRepository;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class AdminPersonService {

    private final PersonRepository personRepository;
    private final FileRepository fileRepository;
    private final KeyRepository keyRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminPersonService.class);

    @Autowired
    public AdminPersonService(PersonRepository personRepository, FileRepository fileRepository, KeyRepository keyRepository) {
        this.personRepository = personRepository;
        this.fileRepository = fileRepository;
        this.keyRepository = keyRepository;
    }

    public void addPerson(RequestBodyAddPerson addPersonRequest) throws AddPersonException {
        doAddPersonChecks(addPersonRequest);
        Key key = addKey(addPersonRequest.getKey(), addPersonRequest.getIv());
        FileEntity face = addFace(addPersonRequest.getFaceFile(), addPersonRequest.getFaceKey(), addPersonRequest.getFaceIv());
        addPerson(addPersonRequest.getFirstName(), addPersonRequest.getLastName(), addPersonRequest.getCommonName(), face, key);
    }

    public void updatePerson(RequestBodyUpdatePerson updatePersonRequest) throws EntityNotFoundException, UpdatePersonException {
        Person oldPerson = getPerson(updatePersonRequest.getId());
        doUpdatePersonChecks(updatePersonRequest);
        Key faceKey = oldPerson.getFace().getKey();
        if(!faceKey.getIv().equals(updatePersonRequest.getFaceIv())){
            faceKey = updateFaceKey(updatePersonRequest.getFaceKey(), updatePersonRequest.getFaceIv(), faceKey);
        }
        updatePerson(oldPerson, updatePersonRequest, faceKey);
    }

    Person getPerson(String personId) throws EntityNotFoundException {
        Optional<Person> personOptional = personRepository.findById(personId);
        if(!personOptional.isPresent()){
            LOGGER.error("Could not get person. Invalid personId {}", personId);
            throw new EntityNotFoundException(Person.class, personId);
        }
        return personOptional.get();
    }

    private void doAddPersonChecks(RequestBodyAddPerson addPersonRequest) throws AddPersonException {
        String firstName = addPersonRequest.getFirstName();
        String lastName = addPersonRequest.getLastName();
        String commonName = addPersonRequest.getCommonName();
        if(personRepository.existsByFirstNameAndLastNameAndCommonName(firstName, lastName, commonName)){
            LOGGER.error("Could not add Person. Person with same names already exists. {}, {}, {}", firstName, lastName, commonName);
            throw new AddPersonException("Person with same names already exists.");
        }
        File file = new File(Application.filesDir + addPersonRequest.getFaceFile());
        if(!file.exists() || file.isDirectory()){
            LOGGER.error("Could not add Person. File {} does not exist or is an directory", addPersonRequest.getFaceFile());
            throw new AddPersonException("Face file does not exist: " + addPersonRequest.getFaceFile());
        }
    }

    private void doUpdatePersonChecks(RequestBodyUpdatePerson updatePersonRequest) throws UpdatePersonException {
        String firstName = updatePersonRequest.getFirstName();
        String lastName = updatePersonRequest.getLastName();
        String commonName = updatePersonRequest.getCommonName();
        Optional<Person> personOptional = personRepository.findByFirstNameAndLastNameAndCommonName(firstName, lastName, commonName);
        if(personOptional.isPresent() && !personOptional.get().getId().equals(updatePersonRequest.getId())){
            LOGGER.error("Could not update Person. Another person with same names already exists. {}, {}, {}", firstName, lastName, commonName);
            throw new UpdatePersonException("Another person with same names already exists.");
        }
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
