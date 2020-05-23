package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentIndex;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.repository.*;
import de.mherrmann.famkidmem.backend.service.edit.EditVideoService;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
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
public class VideoServiceTest {

    private static final String LOGIN_HASH = "loginHash";

    private UserEntity testUser;
    private ResponseBodyLogin testLogin;

    @Autowired
    private UserService userService;

    @Autowired
    private EditVideoService editVideoService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils testUtils;

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
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldGetIndex(){
        Exception exception = null;
        ResponseBodyContentIndex contentIndex = null;

        try {
            contentIndex = videoService.getIndex(testLogin.getAccessToken());
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertIndex(contentIndex);
    }

    @Test
    public void shouldFailGetIndex(){
        Exception exception = null;

        try {
            videoService.getIndex("invalid");
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(SecurityException.class);
    }

    private void assertIndex(ResponseBodyContentIndex contentIndex){
        assertThat(contentIndex).isNotNull();
        assertThat(contentIndex.getVideos().size()).isEqualTo(2);
        assertThat(contentIndex.getVideos().get(0).getTitle()).isEqualTo("title");
        assertThat(contentIndex.getVideos().get(1).getTitle()).isEqualTo("video2");
        assertThat(contentIndex.getPersons().size()).isEqualTo(4);
        assertThat(contentIndex.getPersons().get(0)).isEqualTo("person1");
        assertThat(contentIndex.getPersons().get(1)).isEqualTo("person2");
        assertThat(contentIndex.getPersons().get(2)).isEqualTo("person3");
        assertThat(contentIndex.getPersons().get(3)).isEqualTo("person4");
        assertThat(contentIndex.getYears().get(0)).isEqualTo(1994);
        assertThat(contentIndex.getYears().get(1)).isEqualTo(1995);
        assertThat(contentIndex.getYears().get(2)).isEqualTo(1996);
        assertThat(contentIndex.getYears().get(3)).isEqualTo(1997);
        assertThat(contentIndex.getMasterKey()).isEqualTo(testUser.getMasterKey());
    }

    private void createTestUser() {
        testUser = testUtils.createTestUser(LOGIN_HASH);
    }

}
