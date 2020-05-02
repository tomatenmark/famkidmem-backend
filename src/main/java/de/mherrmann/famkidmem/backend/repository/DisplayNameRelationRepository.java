package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.DisplayNameRelation;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface DisplayNameRelationRepository extends CrudRepository<DisplayNameRelation, String> {

    Optional<DisplayNameRelation> findByMeAndOther(UserEntity me, UserEntity other);

    @Transactional
    void deleteAllByMe(UserEntity me);

}
