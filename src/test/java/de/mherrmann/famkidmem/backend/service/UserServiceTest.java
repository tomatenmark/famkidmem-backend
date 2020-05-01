package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedLogout;
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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    private static final String LOGIN_HASH = "loginHash";

    private UserEntity testUser;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Before
    public void setup(){
        createTestUser();
    }

    @After
    public void teardown(){
        userRepository.deleteAll();
        sessionRepository.deleteAll();
    }

    @Test
    public void shouldLogin(){
        String accessToken = null;
        Exception exception = null;

        try {
            accessToken = userService.login(testUser.getUserName(), LOGIN_HASH);
        } catch (LoginException ex){
            exception = ex;
        }

        assertThat(accessToken).isNotNull();
        assertThat(exception).isNull();
        assertThat(sessionRepository.findByAccessToken(accessToken).isPresent()).isTrue();
        assertThat(sessionRepository.findByAccessToken(accessToken).get().getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    public void shouldFailLoginCausedByInvalidUsername(){
        String accessToken = null;
        Exception exception = null;

        try {
            accessToken = userService.login("wrong", LOGIN_HASH);
        } catch (LoginException ex){
            exception = ex;
        }

        assertThat(accessToken).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(LoginException.class);
    }

    @Test
    public void shouldFailLoginCausedByInvalidLoginHash(){
        String accessToken = null;
        Exception exception = null;

        try {
            accessToken = userService.login(testUser.getUserName(), "wrong");
        } catch (LoginException ex){
            exception = ex;
        }

        assertThat(accessToken).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(LoginException.class);
    }

    @Test
    public void shouldLogoutSingleSession(){
        String accessToken1 = userService.login(testUser.getUserName(), LOGIN_HASH);
        String accessToken2 = userService.login(testUser.getUserName(), LOGIN_HASH);
        Exception exception = null;

        try {
            userService.logout(accessToken1, false);
        } catch(SecurityException ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(sessionRepository.findByAccessToken(accessToken1).isPresent()).isFalse();
        assertThat(sessionRepository.findByAccessToken(accessToken2).isPresent()).isTrue();
        assertThat(userRepository.findByUserName(testUser.getUserName()).get().getSessions().size()).isEqualTo(1);
    }

    @Test
    public void shouldLogoutGlobal() {
        String accessToken1 = userService.login(testUser.getUserName(), LOGIN_HASH);
        String accessToken2 = userService.login(testUser.getUserName(), LOGIN_HASH);
        Exception exception = null;

        try {
            userService.logout(accessToken1, true);
        } catch(SecurityException ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(sessionRepository.findByAccessToken(accessToken1).isPresent()).isFalse();
        assertThat(sessionRepository.findByAccessToken(accessToken2).isPresent()).isFalse();
        assertThat(userRepository.findByUserName(testUser.getUserName()).get().getSessions()).isEmpty();
    }

    @Test
    public void shouldFailLogout(){
        String accessToken = userService.login(testUser.getUserName(), LOGIN_HASH);
        Exception exception = null;

        try {
            userService.logout("wrong token", false);
        } catch(SecurityException ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(SecurityException.class);
        assertThat(sessionRepository.findByAccessToken(accessToken).isPresent()).isTrue();
        assertThat(userRepository.findByUserName(testUser.getUserName()).get().getSessions().size()).isEqualTo(1);
    }


    private void createTestUser(){
        String loginHashHash = Bcrypt.hash(LOGIN_HASH);
        testUser = new UserEntity("userName", "Name", loginHashHash, "masterKey", false, false);
        userRepository.save(testUser);
    }

}
