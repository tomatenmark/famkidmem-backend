package de.mherrmann.famkidmem.backend.service.admin;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddPerson;
import de.mherrmann.famkidmem.backend.entity.FileEntity;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.exception.AddPersonException;
import de.mherrmann.famkidmem.backend.repository.FileRepository;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

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
        doChecks(addPersonRequest);
        Key key = addKey(addPersonRequest.getKey(), addPersonRequest.getIv());
        FileEntity face = addFace(addPersonRequest.getFaceFile(), addPersonRequest.getFaceKey(), addPersonRequest.getFaceIv());
        addPerson(addPersonRequest.getFirstName(), addPersonRequest.getLastName(), addPersonRequest.getCommonName(), face, key);
    }

    private void doChecks(RequestBodyAddPerson addPersonRequest) throws AddPersonException {
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

    private void addPerson(String firstName, String lastName, String commonName, FileEntity face, Key key){
        Person person = new Person(firstName, lastName, commonName, face, key);
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
}
