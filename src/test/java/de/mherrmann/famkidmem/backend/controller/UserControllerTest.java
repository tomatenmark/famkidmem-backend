package de.mherrmann.famkidmem.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.body.RequestBodyLogin;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedChangeValue;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedLogout;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.service.UserService;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    private UserEntity testUser;

    private static final String LOGIN_HASH = "loginHash";

    @Before
    public void setup(){
        createTestUser();
    }

    @After
    public void teardown(){
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldLogin() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createLogin(true))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String accessToken = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getAccessToken();
        String message = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Login was successful");
        assertThat(sessionRepository.findByAccessToken(accessToken).isPresent()).isTrue();
    }

    @Test
    public void shouldFailLogin() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createLogin(false))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String ex = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getException();
        String message = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToLoginResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(sessionRepository.count()).isEqualTo(0);
        assertThat(message).isEqualTo("error");
        assertThat(ex).isEqualTo("LoginException");
        assertThat(details).isEqualTo("Username or Password is wrong");
    }

    @Test
    public void shouldLogout() throws Exception {
        String accessToken = userService.login(testUser.getUserName(), LOGIN_HASH);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createLogout(accessToken))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(sessionRepository.findByAccessToken(accessToken).isPresent()).isFalse();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Logout was successful");
    }

    @Test
    public void shouldFailLogout() throws Exception {
        String accessToken = userService.login(testUser.getUserName(), LOGIN_HASH);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createLogout("wrong"))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(sessionRepository.findByAccessToken(accessToken).isPresent()).isTrue();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo("You are not allowed to do this: logout");
    }

    @Test
    public void shouldChangeUserName() throws Exception {
        String accessToken = userService.login(testUser.getUserName(), LOGIN_HASH);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/change/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createValueChange(accessToken))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        Optional<UserEntity> userOptional = userRepository.findByUserName("newValue");
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully changed username");
        assertThat(userOptional.isPresent()).isTrue();
    }

    @Test
    public void shouldFailChangeUserName() throws Exception {
        userService.login(testUser.getUserName(), LOGIN_HASH);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/user/change/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createValueChange("wrong"))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        Optional<UserEntity> userOptional = userRepository.findByUserName("newValue");
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo("You are not allowed to do this: change username");
        assertThat(userOptional.isPresent()).isFalse();
    }


    private void createTestUser(){
        String loginHashHash = Bcrypt.hash(LOGIN_HASH);
        testUser = new UserEntity("userName", "Name", loginHashHash, "masterKey", false, false);
        userRepository.save(testUser);
    }

    private RequestBodyLogin createLogin(boolean valid){
        RequestBodyLogin login = new RequestBodyLogin();
        login.setLoginHash(valid ? LOGIN_HASH : "wrong");
        login.setUserName(testUser.getUserName());
        return login;
    }

    private RequestBodyAuthorizedLogout createLogout(String accessToken){
        RequestBodyAuthorizedLogout logout = new RequestBodyAuthorizedLogout();
        logout.setAccessToken(accessToken);
        return logout;
    }

    private RequestBodyAuthorizedChangeValue createValueChange(String accessToken){
        RequestBodyAuthorizedChangeValue valueChange = new RequestBodyAuthorizedChangeValue();
        valueChange.setAccessToken(accessToken);
        valueChange.setNewValue("newValue");
        return valueChange;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResponseBodyLogin jsonToLoginResponse(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBodyLogin.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResponseBody jsonToResponse(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBody.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
