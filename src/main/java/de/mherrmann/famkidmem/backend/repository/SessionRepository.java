package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.UserSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface SessionRepository extends CrudRepository<UserSession, String> {
    Optional<UserSession> findByAccessToken(String accessToken);

    @Transactional
    void deleteByAccessToken(String accessToken);

    @Transactional
    void deleteAllByLastRequestBeforeAndUserEntity(Timestamp threshold, UserEntity user);

    @Transactional
    void deleteAllByUserEntity(UserEntity userEntity);
}
