package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.body.RequestBodyLogin;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedChangePassword;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedChangeUsername;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedLogout;
import de.mherrmann.famkidmem.backend.exception.LoginException;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ResponseBodyLogin> login(@RequestBody RequestBodyLogin login) {
        try {
            ResponseBodyLogin body = userService.login(login.getUsername(), login.getLoginHash());
            return ResponseEntity.ok(body);
        } catch(LoginException ex){
            return ResponseEntity.badRequest().body(new ResponseBodyLogin(ex));
        }
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<ResponseBody> logout(@RequestBody RequestBodyAuthorizedLogout logout) {
        try {
            userService.logout(logout.getAccessToken(), logout.isGlobal());
            return ResponseEntity.ok(new ResponseBody("ok", "Logout was successful"));
        } catch(SecurityException ex){
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @PostMapping(value = "/change/username")
    public ResponseEntity<ResponseBody> changeUsername(@RequestBody RequestBodyAuthorizedChangeUsername usernameChange) {
        try {
            userService.changeUsername(usernameChange.getAccessToken(), usernameChange.getNewUsername());
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully changed username"));
        } catch(SecurityException ex){
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @PostMapping(value = "/change/password")
    public ResponseEntity<ResponseBody> changePassword(@RequestBody RequestBodyAuthorizedChangePassword passwordChange) {
        try {
            userService.changePassword(passwordChange.getAccessToken(), passwordChange.getNewLoginHash(), passwordChange.getNewPasswordKeySalt(), passwordChange.getNewMasterKey());
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully changed password"));
        } catch(SecurityException ex){
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

}
