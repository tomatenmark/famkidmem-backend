package de.mherrmann.famkidmem.backend.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.admin.*;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
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

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AdminPersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private PersonRepository personRepository;

    @Before
    public void setup() {
        Application.filesDir = "./files/";
    }

    @After
    public void teardown(){
        testUtils.dropAll();
        testUtils.deleteTestFiles();
    }

    @Test
    public void shouldAddPerson() throws Exception {
        RequestBodyAddPerson addPersonRequest = testUtils.createAddPersonRequest();
        long expectedCount = 1 + personRepository.count();

        MvcResult mvcResult = this.mockMvc.perform(post("/admin/person/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addPersonRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully added person: " + addPersonRequest.getCommonName());
        assertThat(personRepository.count()).isEqualTo(expectedCount);
    }

    @Test
    public void shouldFailAddPersonCausedByEponymousPersonExists() throws Exception {
        RequestBodyAddPerson addPersonRequest = testUtils.createAddPersonRequest();
        testUtils.createTestPerson("testF", "testL", "testC");

        shouldFailAddPerson("Could not add person. Reason: Person with same names already exists.", addPersonRequest);
    }

    @Test
    public void shouldFailAddPersonCausedByMissingFaceFile() throws Exception {
        RequestBodyAddPerson addPersonRequest = testUtils.createAddPersonRequest();
        new File(Application.filesDir+addPersonRequest.getFaceFile()).delete();

        shouldFailAddPerson("Could not add person. Reason: Face file does not exist: " + addPersonRequest.getFaceFile(), addPersonRequest);
    }

    @Test
    public void shouldUpdatePerson() throws Exception {
        Person oldPerson = testUtils.createTestPerson("firstName", "lastName", "commonName");
        RequestBodyUpdatePerson updatePersonRequest = testUtils.createUpdatePersonRequest("firstName", "lastName", "commonName");


        MvcResult mvcResult = this.mockMvc.perform(post("/admin/person/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatePersonRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully updated person: " + updatePersonRequest.getCommonName());
        assertThat(personRepository.findById(oldPerson.getId()).get().getLastName()).isEqualTo(updatePersonRequest.getLastName());
    }

    @Test
    public void shouldFailUpdatePersonCausedByInvalidPersonId() throws Exception {
        Person oldPerson = testUtils.createTestPerson("firstName", "lastName", "commonName");
        RequestBodyUpdatePerson updatePersonRequest = testUtils.createUpdatePersonRequest("firstName", "lastName", "commonName");
        updatePersonRequest.setOldFirstName("invalid");
        String firstName = updatePersonRequest.getOldFirstName();
        String lastName = updatePersonRequest.getOldLastName();
        String commonName = updatePersonRequest.getOldCommonName();

        shouldFailUpdatePerson(String.format("Entity does not exist. Type: Person; designator: %s, %s, %s", firstName, lastName, commonName), updatePersonRequest, oldPerson);
    }

    @Test
    public void shouldFailUpdatePersonCausedByEponymousPersonExists() throws Exception {
        Person oldPerson = testUtils.createTestPerson("firstName", "lastName", "commonName");
        testUtils.createTestPerson("first", "last", "common");
        RequestBodyUpdatePerson updatePersonRequest = testUtils.createUpdatePersonRequest("firstName", "lastName", "commonName");
        updatePersonRequest.setFirstName("first");
        updatePersonRequest.setLastName("last");
        updatePersonRequest.setCommonName("common");

        shouldFailUpdatePerson("Could not update person. Reason: Another person with same names already exists.", updatePersonRequest, oldPerson);
    }

    @Test
    public void shouldDeletePerson() throws Exception {
        Person person = testUtils.createTestPerson("first", "last", "common");
        RequestBodyDeletePerson deletePersonRequest = testUtils.createDeletePersonRequest(person);
        long expectedCount = personRepository.count()-1;

        MvcResult mvcResult = this.mockMvc.perform(delete("/admin/person/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(deletePersonRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("ok");
        assertThat(details).isEqualTo("Successfully deleted person: " + deletePersonRequest.getCommonName());
        assertThat(personRepository.count()).isEqualTo(expectedCount);
    }

    @Test
    public void shouldFailDeletePersonCausedByEntityNotFound() throws Exception {
        Person person = testUtils.createTestPerson("first", "last", "common");
        RequestBodyDeletePerson deletePersonRequest = testUtils.createDeletePersonRequest(person);
        deletePersonRequest.setFirstName("invalid");

        shouldFailDeletePerson("Entity does not exist. Type: Person; designator: invalid, last, common", deletePersonRequest);
    }

    @Test
    public void shouldFailDeletePersonCausedByPersonHasUser() throws Exception {
        Person person = testUtils.createTestPerson("first", "last", "common");
        testUtils.createTestUser(person);
        RequestBodyDeletePerson deletePersonRequest = testUtils.createDeletePersonRequest(person);

        shouldFailDeletePerson("Could not delete person. Reason: This person still has an user. Username: username", deletePersonRequest);
    }

    private void shouldFailAddPerson(String expectedDetails, RequestBodyAddPerson addPersonRequest) throws Exception {
        long countBefore = personRepository.count();

        MvcResult mvcResult = this.mockMvc.perform(post("/admin/person/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addPersonRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(personRepository.count()).isEqualTo(countBefore);
    }

    private void shouldFailUpdatePerson(String expectedDetails, RequestBodyUpdatePerson updatePersonRequest, Person oldPerson) throws Exception {
        long countBefore = personRepository.count();

        MvcResult mvcResult = this.mockMvc.perform(post("/admin/person/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatePersonRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        if(updatePersonRequest.getOldFirstName().equals("invalid")){
            assertThat(personRepository.count()).isEqualTo(countBefore);
        } else {
            Person person= personRepository.findById(oldPerson.getId()).get();
            assertThat(person.getLastName()).isEqualTo("lastName");
        }
    }

    private void shouldFailDeletePerson(String expectedDetails, RequestBodyDeletePerson deletePersonRequest) throws Exception {
        long countBefore = personRepository.count();

        MvcResult mvcResult = this.mockMvc.perform(delete("/admin/person/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(deletePersonRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();

        String message = jsonToResponse(mvcResult.getResponse().getContentAsString()).getMessage();
        String details = jsonToResponse(mvcResult.getResponse().getContentAsString()).getDetails();
        assertThat(message).isEqualTo("error");
        assertThat(details).isEqualTo(expectedDetails);
        assertThat(personRepository.count()).isEqualTo(countBefore);
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
