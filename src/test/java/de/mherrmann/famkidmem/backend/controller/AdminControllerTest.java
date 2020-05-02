package de.mherrmann.famkidmem.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.body.RequestBodyLogin;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedChangePassword;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedChangeUsername;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedLogout;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.DisplayNameRelationRepository;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.service.AdminService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String LOGIN_HASH = "loginHash";

    private ResponseBodyLogin testLogin;
    private UserEntity testUser;

    @Autowired
    private UserService userService;

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
    public void shouldAddUser() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/admin/add-user")
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

        shouldFailAddUser("You are not allowed to do this: add user", addUserRequest);
    }

    @Test
    public void shouldFailAddUserCausedByInvalidDisplayNameRelation() throws Exception {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.getDisplayNames().put("invalid", "invalid");

        shouldFailAddUser("User not found. Username: invalid", addUserRequest);
    }

    @Test
    public void shouldFailAddUserCausedByMissingDisplayNameRelation() throws Exception {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        addUserRequest.getDisplayNames().remove("user");

        shouldFailAddUser("Missing value: some display name relations to set while creating new user", addUserRequest);
    }

    @Test
    public void shouldFailAddUserCausedByNotAdmin() throws Exception {
        RequestBodyAddUser addUserRequest = createAddUserRequest();
        UserEntity user = userRepository.findByUsername(testUser.getUsername()).get();
        user.setAdmin(false);
        userRepository.save(user);

        shouldFailAddUser("You are not allowed to do this: add user", addUserRequest);
    }

    private void shouldFailAddUser(String expectedDetails, RequestBodyAddUser addUserRequest) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/admin/add-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addUserRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(userRepository.count()).isEqualTo(1);
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
