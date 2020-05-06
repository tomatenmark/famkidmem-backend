package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.PersonRelation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRelationRepository extends CrudRepository<PersonRelation, String> {

    Optional<PersonRelation> findFirstByMeAndOther(Person me, Person other);

}
