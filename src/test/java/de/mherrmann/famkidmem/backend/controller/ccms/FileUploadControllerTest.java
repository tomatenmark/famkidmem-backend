package de.mherrmann.famkidmem.backend.controller.ccms;

import de.mherrmann.famkidmem.backend.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileUploadControllerTest {



    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_CONTENT = "Content";
    private static final String TEST_NAME = "test.txt";
    private static final String TEST_DIRECTORY = "./files/";

    @Autowired
    private TestUtils testUtils;

    @Before
    public void setUp() throws IOException {
        testUtils.createTestFilesDirectory();
        testUtils.createAuthTokenHashFile();
    }

    @After
    public void tearDown(){
        testUtils.deleteTestFiles();
        testUtils.deleteAuthTokenHashFile();
    }

    @Test
    public void shouldSaveUploadedFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", TEST_NAME,
                "text/plain", TEST_CONTENT.getBytes());

        MvcResult mvcResult = this.mockMvc.perform(multipart("/ccms/upload/").file(multipartFile)
                .header("CCMS_AUTH_TOKEN", "token"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(new File(TEST_DIRECTORY + TEST_NAME).exists()).isTrue();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("ok");
    }

    @Test
    public void shouldFailCausedByEmptyName() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "",
                "text/plain", TEST_CONTENT.getBytes());

        shouldFail(multipartFile, "error: Can not save file with empty name.");
    }

    @Test
    public void shouldFailCausedByEmptyContent() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", TEST_NAME,
                "text/plain", new byte[]{});

        shouldFail(multipartFile, "error: Can not save empty file.");
    }

    private void shouldFail(MockMultipartFile multipartFile, String expectedResponse) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(multipart("/ccms/upload/").file(multipartFile)
                .header("CCMS_AUTH_TOKEN", "token"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(new File(TEST_DIRECTORY + TEST_NAME).exists()).isFalse();
        assertThat(new File(TEST_DIRECTORY).list().length).isEqualTo(0);
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(expectedResponse);
    }


}
