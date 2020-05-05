package de.mherrmann.famkidmem.backend;

import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyDeleteUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.FileEntity;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.*;
import de.mherrmann.famkidmem.backend.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class TestUtils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private KeyService keyService;

    public void dropAll(){
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        personRepository.deleteAll();
        fileRepository.deleteAll();
        keyRepository.deleteAll();
    }

    public void deleteTestFiles(){
        File directory = new File("./files");
        for(File file : directory.listFiles()){
            file.delete();
        }
        directory.delete();
    }

    public RequestBodyAddUser createAddUserRequest(Person testPerson){
        RequestBodyAddUser addUserRequest = new RequestBodyAddUser();
        addUserRequest.setLoginHash("newLoginHash");
        addUserRequest.setUserKey("newKey");
        addUserRequest.setPasswordKeySalt("newPasswordKeySalt");
        addUserRequest.setUsername("user");
        addUserRequest.setPersonId(testPerson.getId());
        return addUserRequest;
    }

    public RequestBodyResetPassword createResetPasswordRequest(UserEntity testUser){
        RequestBodyResetPassword resetPasswordRequest = new RequestBodyResetPassword();
        resetPasswordRequest.setLoginHash("modifiedLoginHash");
        resetPasswordRequest.setMasterKey("modifiedKey");
        resetPasswordRequest.setPasswordKeySalt("modifiedPasswordKeySalt");
        resetPasswordRequest.setUsername(testUser.getUsername());
        return resetPasswordRequest;
    }

    public RequestBodyDeleteUser createDeleteUserRequest(UserEntity testUser){
        RequestBodyDeleteUser deleteUserRequest = new RequestBodyDeleteUser();
        deleteUserRequest.setUsername(testUser.getUsername());
        return deleteUserRequest;
    }

    public Person createTestPerson(String firstName, String lastName, String commonName) {
        try {
            new File("./files").mkdir();
            new File("./files/test").createNewFile();
            FileEntity fileEntity = new FileEntity(createTestKey(), "test");
            fileRepository.save(fileEntity);
            Person person = new Person(firstName, lastName, commonName, fileEntity, createTestKey());
            personRepository.save(person);
            return person;
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public Key createTestKey(){
        Key keyEntity = new Key("key", "iv");
        return keyRepository.save(keyEntity);
    }

}
