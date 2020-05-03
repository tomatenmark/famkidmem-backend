package de.mherrmann.famkidmem.backend.service.admin;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.exception.AddUserException;
import de.mherrmann.famkidmem.backend.exception.UserNotFoundException;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.service.UserService;
import de.mherrmann.famkidmem.backend.service.admin.AdminUserService;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminUserServiceTest {

    private static final String LOGIN_HASH = "loginHash";

    private ResponseBodyLogin testLogin;
    private UserEntity testUser;
    private Person testPerson;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        testPerson = testUtils.createTestPerson("userF", "userL", "userC");
        createAdminUser();
    }

    @After
    public void teardown(){
        testUtils.dropAll();
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldAddUser(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        Exception exception = null;

        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(userRepository.existsByUsername("user")).isTrue();
        UserEntity user = userRepository.findByUsername("user").get();
        assertThat(Bcrypt.check("newLoginHash", user.getLoginHashHash())).isTrue();
        assertThat(user.getUserKey()).isEqualTo("newKey");
        assertThat(user.getPasswordKeySalt()).isEqualTo("newPasswordKeySalt");
        assertThat(user.getPerson().getFirstName()).isEqualTo("userF");
        assertThat(user.getPerson().getLastName()).isEqualTo("userL");
        assertThat(user.getPerson().getCommonName()).isEqualTo("userC");
    }

    @Test
    public void shouldFailAddUserCausedByInvalidLogin(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.setAccessToken("wrong");

        shouldFailAddUser(SecurityException.class, addUserRequest, "user");
    }

    @Test
    public void shouldFailAddUserCausedByNotAdmin(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        testUser.setAdmin(false);
        userRepository.save(testUser);

        shouldFailAddUser(SecurityException.class, addUserRequest, "user");
    }

    @Test
    public void shouldFailAddUserCausedByInvalidPerson(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.setPersonId("wrong");

        shouldFailAddUser(AddUserException.class, addUserRequest, "user");
    }

    @Test
    public void shouldFailAddUserCausedByPersonAlreadyHasUser(){
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        addUserRequest.setUsername("user2");

        shouldFailAddUser(AddUserException.class, addUserRequest, "user2");
    }

    @Test
    public void shouldFailAddUserCausedByUserAlreadyExists() {
        Person person = testUtils.createTestPerson("user2F", "user2L", "user2C");
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        addUserRequest.setPersonId(person.getId());
        Exception exception = null;

        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(AddUserException.class);
        assertThat(userRepository.findByUsername("admin").isPresent()).isTrue();
        assertThat(userRepository.existsByPerson(person)).isFalse();
    }

    @Test
    public void shouldResetPassword(){
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testLogin, testUser);
        Exception exception = null;

        try {
            adminUserService.resetPassword(resetPasswordRequest);
        } catch (Exception ex){
            exception = ex;
        }

        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        assertThat(exception).isNull();
        assertThat(Bcrypt.check(resetPasswordRequest.getLoginHash(), user.getLoginHashHash())).isTrue();
        assertThat(user.getPasswordKeySalt()).isEqualTo(resetPasswordRequest.getPasswordKeySalt());
        assertThat(user.getUserKey()).isEqualTo(resetPasswordRequest.getUserKey());
        assertThat(user.isInit()).isFalse();
        assertThat(user.isReset()).isTrue();
    }

    @Test
    public void shouldFailResetPasswordCausedByInvalidLogin(){
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testLogin, testUser);
        resetPasswordRequest.setAccessToken("wrong");

        shouldFailResetPassword(SecurityException.class, resetPasswordRequest);
    }

    @Test
    public void shouldFailResetPasswordCausedByNotAdmin(){
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testLogin, testUser);
        testUser.setAdmin(false);
        userRepository.save(testUser);

        shouldFailResetPassword(SecurityException.class, resetPasswordRequest);
    }

    @Test
    public void shouldFailResetPasswordCausedByUserNotFound(){
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testLogin, testUser);
        resetPasswordRequest.setUsername("wrong");

        shouldFailResetPassword(UserNotFoundException.class, resetPasswordRequest);
    }

    private void shouldFailResetPassword(Class exceptionClass, RequestBodyResetPassword resetPasswordRequest){
        Exception exception = null;

        try {
            adminUserService.resetPassword(resetPasswordRequest);
        } catch (Exception ex){
            exception = ex;
        }

        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(exceptionClass);
        assertThat(Bcrypt.check(resetPasswordRequest.getLoginHash(), user.getLoginHashHash())).isFalse();
        assertThat(user.getPasswordKeySalt()).isNotEqualTo(resetPasswordRequest.getPasswordKeySalt());
        assertThat(user.getUserKey()).isNotEqualTo(resetPasswordRequest.getUserKey());
        assertThat(user.isInit()).isFalse();
        assertThat(user.isReset()).isFalse();
    }

    private void shouldFailAddUser(Class exceptionClass, RequestBodyAddUser addUserRequest, String username){
        Exception exception = null;

        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(exceptionClass);
        assertThat(userRepository.existsByUsername("admin")).isTrue();
        assertThat(userRepository.existsByUsername(username)).isFalse();
    }

    private void createAdminUser() {
        String loginHashHash = Bcrypt.hash(LOGIN_HASH);
        Person person = testUtils.createTestPerson("adminF", "adminL", "adminL");
        testUser = new UserEntity("admin", "", loginHashHash, "masterKey", person, true, false);
        testUser.setInit(false);
        testUser.setReset(false);
        userRepository.save(testUser);
        testLogin = userService.login(testUser.getUsername(), LOGIN_HASH);
    }

    private RequestBodyAddUser createAddUserRequest(){
        return testUtils.createAddUserRequest(testPerson, testLogin);
    }

}
