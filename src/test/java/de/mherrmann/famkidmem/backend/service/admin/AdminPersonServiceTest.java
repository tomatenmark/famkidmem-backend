package de.mherrmann.famkidmem.backend.service.admin;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.TestUtils;
import de.mherrmann.famkidmem.backend.body.admin.*;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.exception.AddPersonException;
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

}
