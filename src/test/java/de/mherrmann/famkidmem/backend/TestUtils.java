package de.mherrmann.famkidmem.backend;

import de.mherrmann.famkidmem.backend.body.admin.*;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.*;
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
    private FileRepository fileRepository;

    @Autowired
    private KeyRepository keyRepository;

    public void dropAll() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
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

    public RequestBodyAddUser createAddUserRequest() {
        RequestBodyAddUser addUserRequest = new RequestBodyAddUser();
        addUserRequest.setLoginHash("newLoginHash");
        addUserRequest.setMasterKey("newKey");
        addUserRequest.setPasswordKeySalt("newPasswordKeySalt");
        addUserRequest.setUsername("user");
        addUserRequest.setDisplayName("display");
        return addUserRequest;
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

    public UserEntity createTestUser(){
        UserEntity user = new UserEntity("username", "display","salt", "hash", "masterKey");
        return userRepository.save(user);
    }

    private Key createTestKey(){
        Key keyEntity = new Key("key", "iv");
        return keyRepository.save(keyEntity);
    }

    private void createTestFile() throws IOException {
        new File("./files").mkdir();
        new File("./files/test").createNewFile();
    }

}
