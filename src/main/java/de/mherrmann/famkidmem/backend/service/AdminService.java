package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.entity.DisplayNameRelation;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.UserSession;
import de.mherrmann.famkidmem.backend.exception.MissingValueException;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.exception.UserNotFoundException;
import de.mherrmann.famkidmem.backend.repository.DisplayNameRelationRepository;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final DisplayNameRelationRepository displayNameRelationRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    public AdminService(UserRepository userRepository, SessionRepository sessionRepository, DisplayNameRelationRepository displayNameRelationRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.displayNameRelationRepository = displayNameRelationRepository;
    }

    public void addUser(RequestBodyAddUser addUserRequest) throws UserNotFoundException, SecurityException, MissingValueException {
        checkLogin(addUserRequest.getAccessToken(), "add user");
        String loginHashHash = Bcrypt.hash(addUserRequest.getLoginHash());
        UserEntity user = new UserEntity(addUserRequest.getUsername(), addUserRequest.getPasswordKeySalt(), loginHashHash,
                addUserRequest.getUserKey(), addUserRequest.isAdmin(), addUserRequest.isEditor());
        user.setInit(true);
        userRepository.save(user);
        addDisplayNameRelations(user, addUserRequest.getDisplayNames());
        LOGGER.info("Successfully added user {}", addUserRequest.getUsername());
    }

    private void addDisplayNameRelations(UserEntity user, Map<String, String> displayNames) throws UserNotFoundException, MissingValueException {
        if(displayNames.size() < userRepository.count()){
            LOGGER.error("Missing display name relations for user {}. user not created", user.getUsername());
            userRepository.delete(user);
            throw new MissingValueException("some display name relations to set while creating new user");
        }
        for(Map.Entry<String, String> relation : displayNames.entrySet()){
            String username = relation.getKey();
            String showAs = relation.getValue();
            addDisplayNameRelation(user, username, showAs);
        }
    }

    private void addDisplayNameRelation(UserEntity me, String othersUsername, String showAs) throws UserNotFoundException {
        Optional<UserEntity> userOptional = userRepository.findByUsername(othersUsername);
        if(!userOptional.isPresent()){
            LOGGER.error("Could not create display name relation. user {} not found. user {} not created.", othersUsername, me.getUsername());
            displayNameRelationRepository.deleteAllByMe(me);
            userRepository.delete(me);
            throw new UserNotFoundException(othersUsername);
        }
        UserEntity other = userOptional.get();
        DisplayNameRelation displayNameRelation = new DisplayNameRelation(me, other, showAs);
        displayNameRelationRepository.save(displayNameRelation);
    }

    private void checkLogin(String accessToken, String action) throws SecurityException {
        Optional<UserSession> sessionOptional = sessionRepository.findByAccessToken(accessToken);
        if(!sessionOptional.isPresent()){
            LOGGER.error("Could not add user. Invalid accessToken {}", accessToken);
            throw new SecurityException(action);
        }
        UserSession session = sessionOptional.get();
        UserEntity user = session.getUserEntity();
        if(!user.isAdmin()){
            LOGGER.error("Could not add user. Not an admin: {}", user.getUsername());
            throw new SecurityException(action);
        }
    }

}
