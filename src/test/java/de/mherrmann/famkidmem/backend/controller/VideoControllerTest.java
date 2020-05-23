package de.mherrmann.famkidmem.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentIndex;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.service.UserService;
import de.mherrmann.famkidmem.backend.service.edit.EditVideoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class VideoControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private EditVideoService editVideoService;

    private UserEntity testUser;
    private ResponseBodyLogin testLogin;

    private static final String LOGIN_HASH = "loginHash";

    @Before
    public void setup() throws Exception {
        editVideoService.addVideo(testUtils.createAddVideoRequest());
        editVideoService.addVideo(testUtils.createAddAnotherVideoRequest());
        createTestUser();
        testLogin  = userService.login(testUser.getUsername(), LOGIN_HASH);
    }

    @After
    public void teardown(){
        testUtils.dropAll();
    }

    @Test
    public void shouldGetIndex() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/video/index/{accessToken}", testLogin.getAccessToken()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        ResponseBodyContentIndex contentIndex = jsonToLoginResponseContentIndex(mvcResult.getResponse().getContentAsString());
        assertThat(contentIndex.getMessage()).isEqualTo("ok");
        assertThat(contentIndex.getDetails()).isEqualTo("Successfully got content.");
        assertThat(contentIndex.getMasterKey()).isEqualTo(testUser.getMasterKey());
        assertThat(contentIndex.getVideos().size()).isEqualTo(2);
        assertThat(contentIndex.getPersons().size()).isEqualTo(4);
        assertThat(contentIndex.getYears().size()).isEqualTo(4);
    }

    @Test
    public void shouldFailGetIndex() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/video/index/{accessToken}", "invalid"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        ResponseBodyContentIndex contentIndex = jsonToLoginResponseContentIndex(mvcResult.getResponse().getContentAsString());
        assertThat(contentIndex.getMessage()).isEqualTo("error");
        assertThat(contentIndex.getDetails()).isEqualTo("You are not allowed to do this: get video index");
        assertThat(contentIndex.getMasterKey()).isNull();
        assertThat(contentIndex.getVideos()).isNull();
        assertThat(contentIndex.getPersons()).isNull();
        assertThat(contentIndex.getYears()).isNull();
    }


    private void createTestUser() {
        testUser = testUtils.createTestUser(LOGIN_HASH);
    }

    private static ResponseBodyContentIndex jsonToLoginResponseContentIndex(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBodyContentIndex.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
