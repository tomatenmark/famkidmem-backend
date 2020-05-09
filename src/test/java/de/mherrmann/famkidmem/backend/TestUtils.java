package de.mherrmann.famkidmem.backend;

import de.mherrmann.famkidmem.backend.body.admin.*;
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

    public void dropAll() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        personRepository.deleteAll();
        fileRepository.deleteAll();
        keyRepository.deleteAll();
    }

    public void deleteTestFiles() {
        File directory = new File("./files");
        for (File file : directory.listFiles()) {
            file.delete();
        }
        directory.delete();
    }

    public RequestBodyAddUser createAddUserRequest(Person testPerson) {
        RequestBodyAddUser addUserRequest = new RequestBodyAddUser();
        addUserRequest.setLoginHash("newLoginHash");
        addUserRequest.setUserKey("newKey");
        addUserRequest.setPasswordKeySalt("newPasswordKeySalt");
        addUserRequest.setUsername("user");
        addUserRequest.setPersonId(testPerson.getId());
        return addUserRequest;
    }

    public RequestBodyAddPerson createAddPersonRequest() throws IOException {
        createTestFile();
        RequestBodyAddPerson addPersonRequest = new RequestBodyAddPerson();
        addPersonRequest.setFirstName("testF");
        addPersonRequest.setLastName("testL");
        addPersonRequest.setCommonName("testC");
        addPersonRequest.setFaceFile("test");
        addPersonRequest.setFaceKey("fileKey");
        addPersonRequest.setFaceIv("fileIv");
        addPersonRequest.setKey("key");
        addPersonRequest.setIv("iv");
        return addPersonRequest;
    }

    public RequestBodyUpdatePerson createUpdatePersonRequest(String id) {
        RequestBodyUpdatePerson addUpdateRequest = new RequestBodyUpdatePerson();
        addUpdateRequest.setId(id);
        addUpdateRequest.setFirstName("testF");
        addUpdateRequest.setLastName("newLast");
        addUpdateRequest.setCommonName("testC");
        addUpdateRequest.setFaceKey("newFileKey");
        addUpdateRequest.setFaceIv("newFileIv");
        return addUpdateRequest;
    }

    public RequestBodyResetPassword createResetPasswordRequest(UserEntity testUser) {
        RequestBodyResetPassword resetPasswordRequest = new RequestBodyResetPassword();
        resetPasswordRequest.setLoginHash("modifiedLoginHash");
        resetPasswordRequest.setMasterKey("modifiedKey");
        resetPasswordRequest.setPasswordKeySalt("modifiedPasswordKeySalt");
        resetPasswordRequest.setUsername(testUser.getUsername());
        return resetPasswordRequest;
    }

    public RequestBodyDeleteUser createDeleteUserRequest(UserEntity testUser) {
        RequestBodyDeleteUser deleteUserRequest = new RequestBodyDeleteUser();
        deleteUserRequest.setUsername(testUser.getUsername());
        return deleteUserRequest;
    }

    public Person createTestPerson(String firstName, String lastName, String commonName) throws IOException {
        createTestFile();
        FileEntity fileEntity = new FileEntity(createTestKey(), "test");
        fileRepository.save(fileEntity);
        Person person = new Person(firstName, lastName, commonName, fileEntity, createTestKey());
        personRepository.save(person);
        return person;
    }

    public Key createTestKey() {
        return keyService.createNewKey("key", "iv");
    }

    private void createTestFile() throws IOException {
        new File("./files").mkdir();
        new File("./files/test").createNewFile();
    }

}
