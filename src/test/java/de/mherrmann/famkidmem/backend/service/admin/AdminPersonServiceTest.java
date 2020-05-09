package de.mherrmann.famkidmem.backend.service.admin;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.admin.*;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.exception.AddPersonException;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.exception.UpdatePersonException;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminPersonServiceTest {

    @Autowired
    private AdminPersonService adminPersonService;

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
    public void shouldAddPerson() throws IOException {
        RequestBodyAddPerson addPersonRequest = testUtils.createAddPersonRequest();
        Exception exception = null;

        try {
             adminPersonService.addPerson(addPersonRequest);
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        Optional<Person> personOptional = personRepository.findByFirstNameAndLastNameAndCommonName(addPersonRequest.getFirstName(), addPersonRequest.getLastName(), addPersonRequest.getCommonName());
        assertThat(personOptional.isPresent()).isTrue();
        assertThat(personOptional.get().getFace().getFilename()).isEqualTo(addPersonRequest.getFaceFile());
        assertThat(personOptional.get().getFace().getKey().getKey()).isEqualTo(addPersonRequest.getFaceKey());
        assertThat(personOptional.get().getFace().getKey().getIv()).isEqualTo(addPersonRequest.getFaceIv());
        assertThat(personOptional.get().getKey().getKey()).isEqualTo(addPersonRequest.getKey());
        assertThat(personOptional.get().getKey().getIv()).isEqualTo(addPersonRequest.getIv());
    }

    @Test
    public void shouldFailAddPersonCausedByEponymousPersonExists() throws IOException {
        RequestBodyAddPerson addPersonRequest = testUtils.createAddPersonRequest();
        testUtils.createTestPerson("testF", "testL", "testC");

        shouldFailAddPerson(addPersonRequest);
    }

    @Test
    public void shouldFailAddPersonCausedByMissingFaceFile() throws IOException {
        RequestBodyAddPerson addPersonRequest = testUtils.createAddPersonRequest();
        new File(Application.filesDir+addPersonRequest.getFaceFile()).delete();

        shouldFailAddPerson(addPersonRequest);
    }

    @Test
    public void shouldUpdatePerson() throws IOException {
        Person oldPerson = testUtils.createTestPerson("firstName", "lastName", "commonName");
        RequestBodyUpdatePerson updatePersonRequest = testUtils.createUpdatePersonRequest(oldPerson.getId());
        Exception exception = null;

        try {
            adminPersonService.updatePerson(updatePersonRequest);
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNull();
        Person person = personRepository.findById(oldPerson.getId()).get();
        assertThat(person.getLastName()).isEqualTo("newLast");
        assertThat(person.getFace().getKey().getKey()).isEqualTo("newFileKey");
        assertThat(person.getFace().getKey().getIv()).isEqualTo("newFileIv");
    }

    @Test
    public void shouldFailUpdatePersonCausedByInvalidPersonId() throws IOException {
        Person oldPerson = testUtils.createTestPerson("firstName", "lastName", "commonName");
        RequestBodyUpdatePerson updatePersonRequest = testUtils.createUpdatePersonRequest(oldPerson.getId());
        updatePersonRequest.setId("invalid");

        shouldFailUpdatePerson(oldPerson, updatePersonRequest, EntityNotFoundException.class);
    }

    @Test
    public void shouldFailUpdatePersonCausedByEponymousPersonExists() throws IOException {
        Person oldPerson = testUtils.createTestPerson("firstName", "lastName", "commonName");
        testUtils.createTestPerson("first", "last", "common");
        RequestBodyUpdatePerson updatePersonRequest = testUtils.createUpdatePersonRequest(oldPerson.getId());
        updatePersonRequest.setFirstName("first");
        updatePersonRequest.setLastName("last");
        updatePersonRequest.setCommonName("common");

        shouldFailUpdatePerson(oldPerson, updatePersonRequest, UpdatePersonException.class);
    }

    private void shouldFailAddPerson(RequestBodyAddPerson addPersonRequest){
        long countBefore = personRepository.count();
        Exception exception = null;

        try {
            adminPersonService.addPerson(addPersonRequest);
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(AddPersonException.class);
        assertThat(personRepository.count()).isEqualTo(countBefore);
    }

    private void shouldFailUpdatePerson(Person oldPerson, RequestBodyUpdatePerson updatePersonRequest, Class exceptionClass){
        Exception exception = null;

        try {
            adminPersonService.updatePerson(updatePersonRequest);
        } catch(Exception ex){
            exception = ex;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(exceptionClass);
        if(updatePersonRequest.getId().equals("invalid")){
            assertThat(personRepository.findById(updatePersonRequest.getId()).isPresent()).isFalse();
        } else {
            Person person= personRepository.findById(oldPerson.getId()).get();
            assertThat(person.getLastName()).isEqualTo("lastName");
            assertThat(person.getFace().getKey().getKey()).isEqualTo("key");
            assertThat(person.getFace().getKey().getIv()).isEqualTo("iv");
        }

    }

}
