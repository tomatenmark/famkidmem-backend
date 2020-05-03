package de.mherrmann.famkidmem.backend.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyDeleteUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.service.admin.AdminUserService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AdminUserControllerTest {



    @Autowired
    private MockMvc mockMvc;

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
    public void setup(){
        createAdminUser();
        testPerson = testUtils.createTestPerson("userF", "userL", "userC");
    }

    @After
    public void teardown(){
        testUtils.dropAll();
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldAddUser() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/admin/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createAddUserRequest())))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully added user: " + createAddUserRequest().getUsername());
        assertThat(userRepository.count()).isEqualTo(2);
    }

    @Test
    public void shouldFailAddUserCausedByInvalidLogin() throws Exception {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.setAccessToken("wrong");

        shouldFailAddUser("You are not allowed to do this: add user", addUserRequest, 1);
    }

    @Test
    public void shouldFailAddUserCausedByNotAdmin() throws Exception {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        user.setAdmin(false);
        userRepository.save(user);

        shouldFailAddUser("You are not allowed to do this: add user", addUserRequest, 1);
    }

    @Test
    public void shouldFailAddUserCausedByInvalidPerson() throws Exception {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.setPersonId("wrong");

        shouldFailAddUser("Person does not exist: wrong", addUserRequest, 1);
    }

    @Test
    public void shouldFailAddUserCausedByPersonAlreadyHasUser() throws Exception {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        addUserRequest.setUsername("user2");

        shouldFailAddUser("User for Person already exist: userC", addUserRequest, 2);
    }

    @Test
    public void shouldFailAddUserCausedByUserAlreadyExists() throws Exception {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        Person person = testUtils.createTestPerson("user2F", "user2L", "user2C");
        try {
            adminUserService.addUser(addUserRequest);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        addUserRequest.setPersonId(person.getId());

        shouldFailAddUser("User with username already exist: user", addUserRequest, 2);
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        RequestBodyDeleteUser deleteUserRequest = testUtils.createDeleteUserRequest(testLogin, testUser);
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/admin/user/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(deleteUserRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully deleted user: " + deleteUserRequest.getUsername());
        assertThat(userRepository.existsByUsername(deleteUserRequest.getUsername())).isFalse();
    }

    @Test
    public void shouldFailDeleteUserCausedByInvalidLogin() throws Exception {
        RequestBodyDeleteUser deleteUserRequest = testUtils.createDeleteUserRequest(testLogin, testUser);
        deleteUserRequest.setAccessToken("wrong");

        shouldFailDeleteUser("You are not allowed to do this: delete user", deleteUserRequest);
    }

    @Test
    public void shouldFailDeleteUserCausedByNotAdmin() throws Exception {
        RequestBodyDeleteUser deleteUserRequest = testUtils.createDeleteUserRequest(testLogin, testUser);
        testUser.setAdmin(false);
        userRepository.save(testUser);

        shouldFailDeleteUser("You are not allowed to do this: delete user", deleteUserRequest);
    }

    @Test
    public void shouldFailDeleteUserCausedByUserNotFound() throws Exception {
        RequestBodyDeleteUser deleteUserRequest = testUtils.createDeleteUserRequest(testLogin, testUser);
        deleteUserRequest.setUsername("wrong");

        shouldFailDeleteUser("User does not exist: wrong", deleteUserRequest);
    }

    @Test
    public void shouldResetPassword() throws Exception {
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testLogin, testUser);
        MvcResult mvcResult = this.mockMvc.perform(post("/api/admin/user/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(resetPasswordRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully reset password for user: " + resetPasswordRequest.getUsername());
        assertThat(userRepository.findByUsername(testUser.getUsername()).get().getUserKey()).isEqualTo(resetPasswordRequest.getUserKey());
    }

    @Test
    public void shouldFailResetPasswordCausedByInvalidLogin() throws Exception {
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testLogin, testUser);
        resetPasswordRequest.setAccessToken("wrong");

        shouldFailResetPassword("You are not allowed to do this: reset password", resetPasswordRequest);
    }

    @Test
    public void shouldFailResetPasswordCausedByNotAdmin() throws Exception {
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testLogin, testUser);
        testUser.setAdmin(false);
        userRepository.save(testUser);

        shouldFailResetPassword("You are not allowed to do this: reset password", resetPasswordRequest);
    }

    @Test
    public void shouldFailResetPasswordCausedByUserNotFound() throws Exception {
        RequestBodyResetPassword resetPasswordRequest = testUtils.createResetPasswordRequest(testLogin, testUser);
        resetPasswordRequest.setUsername("wrong");

        shouldFailResetPassword("User does not exist: wrong", resetPasswordRequest);
    }

    private void shouldFailAddUser(String expectedDetails, RequestBodyAddUser addUserRequest, int users) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/admin/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addUserRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(userRepository.count()).isEqualTo(users);
    }

    private void shouldFailDeleteUser(String expectedDetails, RequestBodyDeleteUser deleteUserRequest) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/admin/user/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(deleteUserRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(userRepository.existsByUsername(testUser.getUsername())).isTrue();
    }

    private void shouldFailResetPassword(String expectedDetails, RequestBodyResetPassword resetPasswordRequest) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/admin/user/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(resetPasswordRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(userRepository.findByUsername(testUser.getUsername()).get().getUserKey()).isNotEqualTo(resetPasswordRequest.getUserKey());
    }


    private RequestBodyAddUser createAddUserRequest(){
        return testUtils.createAddUserRequest(testPerson, testLogin);
    }


    private void createAdminUser() {
        Person person = testUtils.createTestPerson("adminF", "adminL", "adminL");
        String loginHashHash = Bcrypt.hash(LOGIN_HASH);
        testUser = new UserEntity("admin", "", loginHashHash, "masterKey", person,true, false);
        testUser.setInit(false);
        testUser.setReset(false);
        userRepository.save(testUser);
        testLogin = userService.login(testUser.getUsername(), LOGIN_HASH);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
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
