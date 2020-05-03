package de.mherrmann.famkidmem.backend.service.admin;

import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.UserSession;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.exception.AddUserException;
import de.mherrmann.famkidmem.backend.exception.UserNotFoundException;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.service.UserService;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PersonRepository personRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserService.class);

    @Autowired
    public AdminUserService(UserRepository userRepository, SessionRepository sessionRepository, PersonRepository personRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.personRepository = personRepository;
    }

    public void addUser(RequestBodyAddUser addUserRequest) throws AddUserException, SecurityException {
        checkLogin(addUserRequest.getAccessToken(), "add user");
        if(userRepository.existsByUsername(addUserRequest.getUsername())){
            LOGGER.error("Could not add user. User with username {} already exists", addUserRequest.getUsername());
            throw new AddUserException("User with username already exist: " + addUserRequest.getUsername());
        }
        Person person = getPerson(addUserRequest.getPersonId());
        String loginHashHash = Bcrypt.hash(addUserRequest.getLoginHash());
        UserEntity user = new UserEntity(addUserRequest.getUsername(), addUserRequest.getPasswordKeySalt(), loginHashHash,
                addUserRequest.getUserKey(), person, addUserRequest.isAdmin(), addUserRequest.isEditor());
        user.setInit(true);
        userRepository.save(user);
        LOGGER.info("Successfully added user {}", addUserRequest.getUsername());
    }

    public void resetPassword(RequestBodyResetPassword resetPasswordRequest) throws SecurityException, UserNotFoundException {
        checkLogin(resetPasswordRequest.getAccessToken(), "reset password");
        Optional<UserEntity> userOptional = userRepository.findByUsername(resetPasswordRequest.getUsername());
        if(!userOptional.isPresent()){
            LOGGER.error("Could not reset password. User {} does not exist.", resetPasswordRequest.getUsername());
            throw new UserNotFoundException(resetPasswordRequest.getUsername());
        }
        UserEntity user = userOptional.get();
        user.setLoginHashHash(Bcrypt.hash(resetPasswordRequest.getLoginHash()));
        user.setPasswordKeySalt(resetPasswordRequest.getPasswordKeySalt());
        user.setUserKey(resetPasswordRequest.getUserKey());
        user.setReset(true);
        userRepository.save(user);
        LOGGER.info("Successfully reset password for user {}", resetPasswordRequest.getUsername());
    }

    private Person getPerson(String personId) throws AddUserException {
        Optional<Person> personOptional = personRepository.findById(personId);
        if(!personOptional.isPresent()){
            LOGGER.error("Could not add user. Invalid personId {}", personId);
            throw new AddUserException("Person does not exist: " + personId);
        }
        Person person = personOptional.get();
        if(userRepository.existsByPerson(person)){
            LOGGER.error("Could not add user. User for person {} {} already exists", person.getFirstName(), person.getLastName());
            throw new AddUserException("User for Person already exist: " + person.getCommonName());
        }
        return person;
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
