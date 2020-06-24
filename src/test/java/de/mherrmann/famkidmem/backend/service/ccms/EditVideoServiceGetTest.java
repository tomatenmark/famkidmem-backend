package de.mherrmann.famkidmem.backend.service.ccms;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentFileBase64;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.exception.AddEntityException;
import de.mherrmann.famkidmem.backend.exception.FileNotFoundException;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
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

    private static final String THUMBNAIL_BASE64 = "dGh1bWJuYWls";
    private static final String M3U8_BASE64 = "bTN1OA==";

    @Autowired
    private EditVideoService editVideoService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        editVideoService.addVideo(testUtils.createAddVideoRequest());
        testUtils.createTestFile("thumbnail");
        testUtils.createTestFile("m3u8");
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

    @Test
    public void shouldGetThumbnail(){
        shouldGetFileBase64("thumbnail", THUMBNAIL_BASE64);
    }

    @Test
    public void shouldGetM3u8(){
        shouldGetFileBase64("m3u8", M3U8_BASE64);
    }

    @Test
    public void shouldFailGetFileBase64CausedByFileNotFound(){
        Exception exception = null;

        try {
            editVideoService.getFileBase64("invalid");
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(FileNotFoundException.class);
    }

    private void shouldGetFileBase64(String filename, String base64){
        Exception exception = null;
        ResponseBodyContentFileBase64 content = null;

        try {
            content = editVideoService.getFileBase64(filename);
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(content).isNotNull();
        assertThat(content.getBase64()).isEqualTo(base64);
    }
}

