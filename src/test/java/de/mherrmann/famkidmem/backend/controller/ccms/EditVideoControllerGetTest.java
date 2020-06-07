package de.mherrmann.famkidmem.backend.controller.ccms;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.service.ccms.EditVideoService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class EditVideoControllerGetTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private EditVideoService editVideoService;

    @Before
    public void setUp() throws Exception {
        editVideoService.addVideo(testUtils.createAddVideoRequest());
        testUtils.createAuthTokenHashFile();
    }

    @After
    public void teardown() {
        testUtils.deleteTestFiles();
        testUtils.deleteAuthTokenHashFile();
        testUtils.dropAll();
    }

    @Test
    public void shouldGetVideos() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/ccms/edit/video/get")
                .header("CCMS_AUTH_TOKEN", "token"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        List<Video> videos = jsonToResponse(mvcResult.getResponse().getContentAsString()).getVideos();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully get videos");
        assertThat(videos.size()).isEqualTo(1);
        assertThat(videos.get(0).getTitle()).isEqualTo("title");
    }

    private static ResponseBodyGetVideos jsonToResponse(final String json) {
        try {
            return new ObjectMapper().readValue(json, ResponseBodyGetVideos.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
