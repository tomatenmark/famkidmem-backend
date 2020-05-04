package de.mherrmann.famkidmem.backend.service.admin;

import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyDeleteUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetUsers;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.UserSession;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.exception.AddUserException;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PersonRepository personRepository;
    private final KeyRepository keyRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserService.class);

    @Autowired
    public AdminUserService(UserRepository userRepository, SessionRepository sessionRepository, PersonRepository personRepository, KeyRepository keyRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.personRepository = personRepository;
        this.keyRepository = keyRepository;
    }

    public void addUser(RequestBodyAddUser addUserRequest) throws AddUserException, SecurityException, EntityNotFoundException {
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

    public void deleteUser(RequestBodyDeleteUser deleteUserRequest) throws SecurityException, EntityNotFoundException {
        checkLogin(deleteUserRequest.getAccessToken(), "delete user");
        UserEntity user = getUser(deleteUserRequest.getUsername());
        sessionRepository.deleteAllByUserEntity(user);
        userRepository.delete(user);
        LOGGER.info("Successfully deleted user {}", deleteUserRequest.getUsername());
    }

    public ResponseBodyGetUsers getUsers(String accessToken) throws SecurityException, EntityNotFoundException {
        UserEntity user = checkLogin(accessToken, "get users");
        Key key = getKey("persons");
        List<UserEntity> users = new ArrayList<>();
        Iterable<UserEntity> userEntities = userRepository.findAll();
        userEntities.forEach(users::add);
        ResponseBodyGetUsers usersResponse = new ResponseBodyGetUsers(users, key, user.getUserKey());
        usersResponse.setUsers(users);
        usersResponse.setPersonKey(key);
        usersResponse.setUserKey(user.getUserKey());
        LOGGER.info("Successfully get users");
        return usersResponse;
    }

    public void resetPassword(RequestBodyResetPassword resetPasswordRequest) throws SecurityException, EntityNotFoundException {
        checkLogin(resetPasswordRequest.getAccessToken(), "reset password");
        UserEntity user = getUser(resetPasswordRequest.getUsername());
        user.setLoginHashHash(Bcrypt.hash(resetPasswordRequest.getLoginHash()));
        user.setPasswordKeySalt(resetPasswordRequest.getPasswordKeySalt());
        user.setUserKey(resetPasswordRequest.getUserKey());
        user.setReset(true);
        userRepository.save(user);
        LOGGER.info("Successfully reset password for user {}", resetPasswordRequest.getUsername());
    }

    private UserEntity getUser(String username) throws EntityNotFoundException {
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if(!userOptional.isPresent()){
            LOGGER.error("Could not reset password. User {} does not exist.", username);
            throw new EntityNotFoundException(UserEntity.class, username);
        }
        return userOptional.get();
    }

    private Key getKey(String usage) throws EntityNotFoundException {
        Optional<Key> keyOptional = keyRepository.findByUsage(usage);
        if(!keyOptional.isPresent()){
            LOGGER.error("Could not get key for {} usage.", usage);
            throw new EntityNotFoundException(Key.class, usage);
        }
        return keyOptional.get();
    }

    private Person getPerson(String personId) throws AddUserException, EntityNotFoundException {
        Optional<Person> personOptional = personRepository.findById(personId);
        if(!personOptional.isPresent()){
            LOGGER.error("Could not add user. Invalid personId {}", personId);
            throw new EntityNotFoundException(Person.class, personId);
        }
        Person person = personOptional.get();
        if(userRepository.existsByPerson(person)){
            LOGGER.error("Could not add user. User for person {} {} already exists", person.getFirstName(), person.getLastName());
            throw new AddUserException("User for Person already exist: " + person.getCommonName());
        }
        return person;
    }

    private UserEntity checkLogin(String accessToken, String action) throws SecurityException {
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
        return user;
    }

}
