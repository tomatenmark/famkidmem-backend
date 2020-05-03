package de.mherrmann.famkidmem.backend;

import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.Picture;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import de.mherrmann.famkidmem.backend.repository.PictureRepository;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
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
    private PictureRepository pictureRepository;

    public void dropAll(){
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        personRepository.deleteAll();
        pictureRepository.deleteAll();
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

    public Person createTestPerson(String firstName, String lastName, String commonName) {
        try {
            new File("./files").mkdir();
            new File("./files/test").createNewFile();
            Picture picture = new Picture("key", "iv", "test");
            pictureRepository.save(picture);
            Person person = new Person(firstName, lastName, commonName, picture);
            personRepository.save(person);
            return person;
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

}
