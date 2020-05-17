package de.mherrmann.famkidmem.backend.service.edit;

import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.exception.AddEntityException;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import org.junit.After;
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
public class EditVideoServiceTest {

    @Autowired
    private EditVideoService editVideoService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TestUtils testUtils;

    @After
    public void teardown(){
        testUtils.dropAll();
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldAddVideo() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        Exception exception = null;

        try {
            editVideoService.addVideo(addVideoRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        assertThat(videoRepository.existsByTitle(addVideoRequest.getTitle())).isTrue();
        assertToBe(addVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingTitle() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setTitle("");

        shouldFailAddVideo(addVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingKey() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setKey("");

        shouldFailAddVideo(addVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingIv() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setIv("");

        shouldFailAddVideo(addVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingM3u8Key() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setM3u8Key("");

        shouldFailAddVideo(addVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingM3u8Iv() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setM3u8Iv("");

        shouldFailAddVideo(addVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingThumbnailKey() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setThumbnailKey("");

        shouldFailAddVideo(addVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingThumbnailIv() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        addVideoRequest.setThumbnailIv("");

        shouldFailAddVideo(addVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingThumbnailFile() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        new File("./files/thumbnail").delete();

        shouldFailAddVideo(addVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByMissingM3u8File() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        new File("./files/m3u8").delete();

        shouldFailAddVideo(addVideoRequest);
    }

    @Test
    public void shouldFailAddVideoCausedByEponymousVideoExists() throws IOException {
        RequestBodyAddVideo addVideoRequest = testUtils.createAddVideoRequest();
        try {
            editVideoService.addVideo(addVideoRequest);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        shouldFailAddVideo(addVideoRequest);
    }

    private void assertToBe(RequestBodyAddVideo addVideoRequest){
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getTitle()).isEqualTo(addVideoRequest.getTitle());
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getKey().getKey()).isEqualTo(addVideoRequest.getKey());
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getKey().getIv()).isEqualTo(addVideoRequest.getIv());
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getM3u8().getKey().getKey()).isEqualTo(addVideoRequest.getM3u8Key());
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getM3u8().getKey().getIv()).isEqualTo(addVideoRequest.getM3u8Iv());
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getM3u8().getFilename()).isEqualTo(addVideoRequest.getM3u8Filename());
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getThumbnail().getKey().getKey()).isEqualTo(addVideoRequest.getThumbnailKey());
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getThumbnail().getKey().getIv()).isEqualTo(addVideoRequest.getThumbnailIv());
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getThumbnail().getFilename()).isEqualTo(addVideoRequest.getThumbnailFilename());
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getPersons()).isNotEmpty();
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getYears()).isNotEmpty();
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getPersons().get(0).getName()).isEqualTo(addVideoRequest.getPersons().get(0));
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getPersons().get(1).getName()).isEqualTo(addVideoRequest.getPersons().get(1));
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getYears().get(0).getValue()).isEqualTo(addVideoRequest.getYears().get(0));
        assertThat(videoRepository.findByTitle(addVideoRequest.getTitle()).get().getYears().get(1).getValue()).isEqualTo(addVideoRequest.getYears().get(1));
    }

    private void shouldFailAddVideo(RequestBodyAddVideo addVideoRequest){
        Exception exception = null;
        long videos = videoRepository.count();

        try {
            editVideoService.addVideo(addVideoRequest);
        } catch (Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(AddEntityException.class);
        assertThat(videoRepository.count()).isEqualTo(videos);
    }
}

