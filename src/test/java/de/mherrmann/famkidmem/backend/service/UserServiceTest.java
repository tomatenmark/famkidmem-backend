package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.exception.LoginException;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
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

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {


    private static final String LOGIN_HASH = "loginHash";

    private UserEntity testUser;

    @Autowired
    private UserService userService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Before
    public void setup() throws IOException {
        createTestUser();
    }

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldLogin(){
        ResponseBodyLogin login = null;
        Exception exception = null;
        String oldLoginHashHash = userRepository.findByUsername(testUser.getUsername()).get().getLoginHashHash();

        try {
            login = userService.login(testUser.getUsername(), LOGIN_HASH);
        } catch (LoginException ex){
            exception = ex;
        }

        String newLoginHashHash = userRepository.findByUsername(testUser.getUsername()).get().getLoginHashHash();
        assertThat(login).isNotNull();
        assertThat(exception).isNull();
        assertThat(sessionRepository.findByAccessToken(login.getAccessToken()).isPresent()).isTrue();
        assertThat(newLoginHashHash).isNotEqualTo(oldLoginHashHash);
    }

    @Test
    public void shouldFailLoginCausedByInvalidUsername(){
        ResponseBodyLogin login = null;
        Exception exception = null;

        try {
            login = userService.login("wrong", LOGIN_HASH);
        } catch (LoginException ex){
            exception = ex;
        }

        assertThat(login).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(LoginException.class);
    }

    @Test
    public void shouldFailLoginCausedByInvalidLoginHash(){
        ResponseBodyLogin login = null;
        Exception exception = null;

        try {
            login = userService.login(testUser.getUsername(), "wrong");
        } catch (LoginException ex){
            exception = ex;
        }

        assertThat(login).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(LoginException.class);
    }

    @Test
    public void shouldLogoutSingleSession() {
        ResponseBodyLogin login1  = userService.login(testUser.getUsername(), LOGIN_HASH);
        ResponseBodyLogin login2  = userService.login(testUser.getUsername(), LOGIN_HASH);
        Exception exception = null;

        try {
            userService.logout(login1.getAccessToken(), false);
        } catch(SecurityException ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(sessionRepository.findByAccessToken(login1.getAccessToken()).isPresent()).isFalse();
        assertThat(sessionRepository.findByAccessToken(login2.getAccessToken()).isPresent()).isTrue();
        assertThat(sessionRepository.countAllByUserEntity(userRepository.findByUsername(testUser.getUsername()).get())).isEqualTo(1);
    }

    @Test
    public void shouldLogoutGlobal() {
        ResponseBodyLogin login1  = userService.login(testUser.getUsername(), LOGIN_HASH);
        ResponseBodyLogin login2  = userService.login(testUser.getUsername(), LOGIN_HASH);
        Exception exception = null;

        try {
            userService.logout(login1.getAccessToken(), true);
        } catch(SecurityException ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(sessionRepository.findByAccessToken(login1.getAccessToken()).isPresent()).isFalse();
        assertThat(sessionRepository.findByAccessToken(login2.getAccessToken()).isPresent()).isFalse();
        assertThat(sessionRepository.countAllByUserEntity(userRepository.findByUsername(testUser.getUsername()).get())).isEqualTo(0);
    }

    @Test
    public void shouldFailLogout(){
        ResponseBodyLogin login  = userService.login(testUser.getUsername(), LOGIN_HASH);
        Exception exception = null;

        try {
            userService.logout("wrong token", false);
        } catch(SecurityException ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(SecurityException.class);
        assertThat(sessionRepository.findByAccessToken(login.getAccessToken()).isPresent()).isTrue();
        assertThat(sessionRepository.countAllByUserEntity(userRepository.findByUsername(testUser.getUsername()).get())).isEqualTo(1);
    }

    @Test
    public void shouldChangeUsername(){
        String accessToken = userService.login(testUser.getUsername(), LOGIN_HASH).getAccessToken();

        Exception exception = null;

        try {
            userService.changeUsername(accessToken, "newValue");
        } catch(SecurityException ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(userRepository.findByUsername("newValue").isPresent()).isTrue();
        assertThat(userRepository.findByUsername("newValue").get().isInit()).isFalse();
    }

    @Test
    public void shouldFailChangeUsername(){
        userService.login(testUser.getUsername(), LOGIN_HASH);
        Exception exception = null;

        try {
            userService.changeUsername("wrong", "newValue");
        } catch(SecurityException ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(SecurityException.class);
        assertThat(userRepository.findByUsername("newValue").isPresent()).isFalse();
    }

    @Test
    public void shouldChangePassword(){
        ResponseBodyLogin login = userService.login(testUser.getUsername(), LOGIN_HASH);

        Exception exception = null;

        try {
            userService.changePassword(login.getAccessToken(), "newValue", "salt", "key");
        } catch(SecurityException ex){
            exception = ex;
        }

        String loginHashHash = userRepository.findByUsername(testUser.getUsername()).get().getLoginHashHash();
        String key = userRepository.findByUsername(testUser.getUsername()).get().getMasterKey();
        assertThat(exception).isNull();
        assertThat(Bcrypt.check("newValue", loginHashHash)).isTrue();
        assertThat(userRepository.findByUsername(testUser.getUsername()).get().isReset()).isFalse();
        assertThat(key).isEqualTo("key");
    }

    @Test
    public void shouldFailChangePassword(){
        userService.login(testUser.getUsername(), LOGIN_HASH);

        Exception exception = null;

        try {
            userService.changePassword("wrong", "newValue", "salt", "key");
        } catch(SecurityException ex){
            exception = ex;
        }

        String loginHashHash = userRepository.findByUsername(testUser.getUsername()).get().getLoginHashHash();
        String key = userRepository.findByUsername(testUser.getUsername()).get().getMasterKey();
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(SecurityException.class);
        assertThat(Bcrypt.check("newValue", loginHashHash)).isFalse();
        assertThat(Bcrypt.check(LOGIN_HASH, loginHashHash)).isTrue();
        assertThat(key).isNotEqualTo("key");
    }


    private void createTestUser() {
        String loginHashHash = Bcrypt.hash(LOGIN_HASH);
        testUser = new UserEntity("username", "", "salt", loginHashHash, "masterKey");
        testUser.setInit(true);
        testUser.setReset(true);
        userRepository.save(testUser);
    }

}
