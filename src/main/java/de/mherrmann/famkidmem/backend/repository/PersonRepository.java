package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, String> {

    Optional<Person> findById(String id);
    boolean existsByFirstNameAndLastNameAndCommonName(String first, String last, String common);
    Optional<Person> findByFirstNameAndLastNameAndCommonName(String first, String last, String common);

}
