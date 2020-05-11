package de.mherrmann.famkidmem.backend.service.admin;

import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyDeleteUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetUsers;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.exception.AddUserException;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.service.KeyService;
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
    private final AdminPersonService adminPersonService;
    private final KeyService keyService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserService.class);

    @Autowired
    public AdminUserService(UserRepository userRepository, SessionRepository sessionRepository, AdminPersonService adminPersonService, KeyService keyService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.adminPersonService = adminPersonService;
        this.keyService = keyService;
    }

    public void addUser(RequestBodyAddUser addUserRequest) throws AddUserException, EntityNotFoundException {
        if(userRepository.existsByUsername(addUserRequest.getUsername())){
            LOGGER.error("Could not add user. User with username {} already exists", addUserRequest.getUsername());
            throw new AddUserException("User with username already exist: " + addUserRequest.getUsername());
        }
        Person person = getPerson(addUserRequest);
        String loginHashHash = Bcrypt.hash(addUserRequest.getLoginHash());
        UserEntity user = new UserEntity(addUserRequest.getUsername(), addUserRequest.getPasswordKeySalt(), loginHashHash,
                addUserRequest.getUserKey(), person, keyService.createNewKey(addUserRequest.getKey(), addUserRequest.getIv()));
        user.setInit(true);
        userRepository.save(user);
        LOGGER.info("Successfully added user {}", addUserRequest.getUsername());
    }

    public void deleteUser(RequestBodyDeleteUser deleteUserRequest) throws EntityNotFoundException {
        UserEntity user = getUser(deleteUserRequest.getUsername());
        sessionRepository.deleteAllByUserEntity(user);
        userRepository.delete(user);
        LOGGER.info("Successfully deleted user {}", deleteUserRequest.getUsername());
    }

    public ResponseBodyGetUsers getUsers() {
        List<UserEntity> users = new ArrayList<>();
        Iterable<UserEntity> userEntities = userRepository.findAll();
        userEntities.forEach(users::add);
        ResponseBodyGetUsers usersResponse = new ResponseBodyGetUsers(users);
        LOGGER.info("Successfully get users");
        return usersResponse;
    }

    public void resetPassword(RequestBodyResetPassword resetPasswordRequest) throws EntityNotFoundException {
        UserEntity user = getUser(resetPasswordRequest.getUsername());
        user.setLoginHashHash(Bcrypt.hash(resetPasswordRequest.getLoginHash()));
        user.setPasswordKeySalt(resetPasswordRequest.getPasswordKeySalt());
        user.setMasterKey(resetPasswordRequest.getMasterKey());
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

    private Person getPerson(RequestBodyAddUser addUserRequest) throws AddUserException, EntityNotFoundException {
        String firstName = addUserRequest.getPersonFirstName();
        String lastName = addUserRequest.getPersonLastName();
        String commonName = addUserRequest.getPersonCommonName();
        Person person = adminPersonService.getPerson(firstName, lastName, commonName);
        if(userRepository.existsByPerson(person)){
            LOGGER.error("Could not add user. User for person {} {} already exists", person.getFirstName(), person.getLastName());
            throw new AddUserException("User for Person already exist: " + person.getCommonName());
        }
        return person;
    }

}
