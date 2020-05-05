package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import de.mherrmann.famkidmem.backend.entity.UserSession;
import de.mherrmann.famkidmem.backend.exception.LoginException;
import de.mherrmann.famkidmem.backend.repository.SessionRepository;
import de.mherrmann.famkidmem.backend.repository.UserRepository;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    private static final long SESSION_TIME_TO_LIVE = 24*60*60*1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public ResponseBodyLogin login(String username, String loginHash) throws LoginException {
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if(!userOptional.isPresent()){
            LOGGER.error("Could not login user. Invalid username {}", username);
            throw new LoginException();
        }
        UserEntity user = userOptional.get();
        String loginHashHash = user.getLoginHashHash();
        if(!Bcrypt.check(loginHash, loginHashHash)){
            LOGGER.error("Could not login user. Invalid loginHash {}", loginHash);
            throw new LoginException();
        }
        String accessToken = UUID.randomUUID().toString();
        addUserSession(user, accessToken);
        createNewHash(user, loginHash);
        LOGGER.info("Successfully logged in {} with accessToken {}", user.getUsername(), accessToken);
        return new ResponseBodyLogin(accessToken, user.getPasswordKeySalt());
    }

    public void logout(String accessToken, boolean global) throws SecurityException {
        Optional<UserSession> sessionOptional = sessionRepository.findByAccessToken(accessToken);
        if(!sessionOptional.isPresent()){
            LOGGER.error("Could not logout user. Invalid accessToken {}", accessToken);
            throw new SecurityException("logout");
        }
        UserSession session = sessionOptional.get();
        if(global){
            UserEntity user = session.getUserEntity();
            LOGGER.info("Successfully logged out {} from all sessions", session.getUserEntity().getUsername());
            sessionRepository.deleteAllByUserEntity(user);
        } else {
            sessionRepository.delete(session);
            LOGGER.info("Successfully logged out {} from accessToken {}", session.getUserEntity().getUsername(), accessToken);
        }
    }

    public void changeUsername(String accessToken, String newUsername) throws SecurityException {
        Optional<UserSession> sessionOptional = sessionRepository.findByAccessToken(accessToken);
        if(!sessionOptional.isPresent()){
            LOGGER.error("Could not change username. Invalid accessToken {}", accessToken);
            throw new SecurityException("change username");
        }
        UserEntity user = sessionOptional.get().getUserEntity();
        String oldUsername = user.getUsername();
        user.setUsername(newUsername);
        if(user.isInit()){
            user.setInit(false);
            user.setReset(true);
        }
        userRepository.save(user);
        LOGGER.info("Successfully changed username from {} to {}", oldUsername, newUsername);
    }

    public void changePassword(String accessToken, String newLoginHash, String newPasswordKeySalt, String newMasterKey) throws SecurityException {
        Optional<UserSession> sessionOptional = sessionRepository.findByAccessToken(accessToken);
        if(!sessionOptional.isPresent()){
            LOGGER.error("Could not change password. Invalid accessToken {}", accessToken);
            throw new SecurityException("change password");
        }
        UserEntity user = sessionOptional.get().getUserEntity();
        user.setLoginHashHash(Bcrypt.hash(newLoginHash));
        user.setMasterKey(newMasterKey);
        user.setPasswordKeySalt(newPasswordKeySalt);
        user.setReset(false);
        userRepository.save(user);
        LOGGER.info("Successfully changed password for user {}", user.getUsername());
    }

    private void createNewHash(UserEntity user, String loginHash){
        String newHash = Bcrypt.hash(loginHash);
        user.setLoginHashHash(newHash);
        userRepository.save(user);
    }

    private void addUserSession(UserEntity user, String accessToken){
        UserSession session = new UserSession(user, accessToken);
        sessionRepository.save(session);
        long threshold = System.currentTimeMillis() - SESSION_TIME_TO_LIVE;
        sessionRepository.deleteAllByLastRequestBeforeAndUserEntity(new Timestamp(threshold), user);
    }

    /*

    Admin
displayname: Admin
username: 40j@8v3bPP5E2$
loginHash: Sruv2RRpoau+67KjnCc/I7FoXc8O2fYrQ/qKRLXBPvY=
userMasterKey: nb/wXqv7yxSzTA2+9sZATg==
isAdmin: true
isEditor: true

Mark
displayname: Admin
username: Kh#vu2eA*!Glx1
loginHash: 7itleld/CDe06OFsMQdg8gMi1Yvid8zjUeek3FwPdoM= app.js:12:13
userMasterKey: Oxa4WslY/Ndxi4DMFd2XtQ==
isAdmin: false
isEditor: false


     */
}
