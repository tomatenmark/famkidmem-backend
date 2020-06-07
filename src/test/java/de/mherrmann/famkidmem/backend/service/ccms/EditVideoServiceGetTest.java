package de.mherrmann.famkidmem.backend.service.ccms;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.exception.AddEntityException;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EditVideoServiceGetTest {

    @Autowired
    private EditVideoService editVideoService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        editVideoService.addVideo(testUtils.createAddVideoRequest());
    }

    @After
    public void teardown(){
        testUtils.dropAll();
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldGetVideos() {
        ResponseBodyGetVideos responseBodyGetVideos = null;
        Exception exception = null;

        try {
            responseBodyGetVideos = editVideoService.getVideos();
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(responseBodyGetVideos).isNotNull();
        assertThat(responseBodyGetVideos.getVideos()).isNotNull();
        assertThat(responseBodyGetVideos.getVideos().size()).isEqualTo(1);
        assertThat(responseBodyGetVideos.getVideos().get(0).getTitle()).isEqualTo("title");
    }

}

