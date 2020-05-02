package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.exception.MissingValueException;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.exception.UserNotFoundException;
import de.mherrmann.famkidmem.backend.repository.DisplayNameRelationRepository;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminServiceTest {

    private static final String LOGIN_HASH = "loginHash";

    private ResponseBodyLogin testLogin;
    private UserEntity testUser;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private DisplayNameRelationRepository displayNameRelationRepository;

    @Before
    public void setup(){
        createAdminUser();
    }

    @After
    public void teardown(){
        sessionRepository.deleteAll();
        displayNameRelationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldAddUser(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        Exception exception = null;
        UserEntity admin = userRepository.findByUsername("admin").get();

        try {
            adminService.addUser(addUserRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(userRepository.findByUsername("user").isPresent()).isTrue();
        UserEntity user = userRepository.findByUsername("user").get();
        assertThat(displayNameRelationRepository.count()).isEqualTo(2);
        assertThat(displayNameRelationRepository.findByMeAndOther(user, admin).get().getName()).isEqualTo("ADMIN");
        assertThat(displayNameRelationRepository.findByMeAndOther(user, user).get().getName()).isEqualTo("just a user");
        assertThat(Bcrypt.check("newLoginHash", user.getLoginHashHash())).isTrue();
        assertThat(user.getUserKey()).isEqualTo("newKey");
        assertThat(user.getPasswordKeySalt()).isEqualTo("newPasswordKeySalt");
    }

    @Test
    public void shouldFailAddUserCausedByInvalidLogin(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.setAccessToken("wrong");

        shouldFailAddUser(SecurityException.class, addUserRequest);
    }

    @Test
    public void shouldFailAddUserCausedByInvalidRelation(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.getDisplayNames().put("invalid", "invalid");

        shouldFailAddUser(UserNotFoundException.class, addUserRequest);
    }

    @Test
    public void shouldFailAddUserCausedByMissingRelation(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.getDisplayNames().remove("admin");

        shouldFailAddUser(MissingValueException.class, addUserRequest);
    }

    @Test
    public void shouldFailAddUserCausedByNotAdmin(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        user.setAdmin(false);
        userRepository.save(user);

        shouldFailAddUser(SecurityException.class, addUserRequest);
    }

    private void shouldFailAddUser(Class exceptionClass, RequestBodyAddUser addUserRequest){
        Exception exception = null;

        try {
            adminService.addUser(addUserRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(exceptionClass);
        assertThat(userRepository.findByUsername("admin").isPresent()).isTrue();
        assertThat(userRepository.findByUsername("user").isPresent()).isFalse();
        assertThat(displayNameRelationRepository.count()).isEqualTo(0);
    }

    private void createAdminUser(){
        String loginHashHash = Bcrypt.hash(LOGIN_HASH);
        testUser = new UserEntity("admin", "", loginHashHash, "masterKey", true, false);
        testUser.setInit(false);
        testUser.setReset(false);
        userRepository.save(testUser);
        testLogin = userService.login(testUser.getUsername(), LOGIN_HASH);
    }

    private RequestBodyAddUser createAddUserRequest(){
        RequestBodyAddUser addUserRequest = new RequestBodyAddUser();
        addUserRequest.setAccessToken(testLogin.getAccessToken());
        addUserRequest.setLoginHash("newLoginHash");
        addUserRequest.setUserKey("newKey");
        addUserRequest.setPasswordKeySalt("newPasswordKeySalt");
        addUserRequest.setUsername("user");
        addUserRequest.setDisplayNames(createDisplayNameRelations());
        return addUserRequest;
    }

    private Map<String, String> createDisplayNameRelations(){
        Map<String, String> relations = new HashMap<>();
        relations.put("admin", "ADMIN");
        relations.put("user", "just a user");
        return relations;
    }

}
