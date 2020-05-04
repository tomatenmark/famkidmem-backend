package de.mherrmann.famkidmem.backend;

import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyDeleteUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetUsers;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.Picture;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestUtils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private KeyRepository keyRepository;

    public void dropAll(){
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        personRepository.deleteAll();
        pictureRepository.deleteAll();
        keyRepository.deleteAll();
    }

    public void deleteTestFiles(){
        File directory = new File("./files");
        for(File file : directory.listFiles()){
            file.delete();
        }
        directory.delete();
    }

    public RequestBodyAddUser createAddUserRequest(Person testPerson, ResponseBodyLogin testLogin){
        RequestBodyAddUser addUserRequest = new RequestBodyAddUser();
        addUserRequest.setAccessToken(testLogin.getAccessToken());
        addUserRequest.setLoginHash("newLoginHash");
        addUserRequest.setUserKey("newKey");
        addUserRequest.setPasswordKeySalt("newPasswordKeySalt");
        addUserRequest.setUsername("user");
        addUserRequest.setPersonId(testPerson.getId());
        return addUserRequest;
    }

    public RequestBodyResetPassword createResetPasswordRequest(ResponseBodyLogin testLogin, UserEntity testUser){
        RequestBodyResetPassword resetPasswordRequest = new RequestBodyResetPassword();
        resetPasswordRequest.setAccessToken(testLogin.getAccessToken());
        resetPasswordRequest.setLoginHash("modifiedLoginHash");
        resetPasswordRequest.setUserKey("modifiedKey");
        resetPasswordRequest.setPasswordKeySalt("modifiedPasswordKeySalt");
        resetPasswordRequest.setUsername(testUser.getUsername());
        return resetPasswordRequest;
    }

    public RequestBodyDeleteUser createDeleteUserRequest(ResponseBodyLogin testLogin, UserEntity testUser){
        RequestBodyDeleteUser deleteUserRequest = new RequestBodyDeleteUser();
        deleteUserRequest.setAccessToken(testLogin.getAccessToken());
        deleteUserRequest.setUsername(testUser.getUsername());
        return deleteUserRequest;
    }

    public Person createTestPerson(String firstName, String lastName, String commonName) {
        try {
            new File("./files").mkdir();
            new File("./files/test").createNewFile();
            Key key = new Key("picture","key", "iv");
            keyRepository.save(key);
            Picture picture = new Picture(key, "test");
            pictureRepository.save(picture);
            Person person = new Person(firstName, lastName, commonName, picture);
            personRepository.save(person);
            return person;
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void createTextKeys(){
        Key personKey = new Key("persons", "key", "iv");
        Key indexKey = new Key("index", "key", "iv");
        keyRepository.save(personKey);
        keyRepository.save(indexKey);
    }

}
