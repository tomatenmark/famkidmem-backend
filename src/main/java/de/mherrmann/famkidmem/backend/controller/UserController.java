package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.body.RequestBodyLogin;
import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.ResponseBodyLogin;
import de.mherrmann.famkidmem.backend.body.authorized.RequestBodyAuthorizedLogout;
import de.mherrmann.famkidmem.backend.exception.LoginException;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            String accessToken = userService.login(login.getUserName(), login.getLoginHash());
            return ResponseEntity.ok(new ResponseBodyLogin(accessToken));
        } catch(LoginException ex){
            return ResponseEntity.ok(new ResponseBodyLogin(ex));
        }
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<ResponseBody> logout(@RequestBody RequestBodyAuthorizedLogout logout) {
        try {
            userService.logout(logout.getAccessToken(), logout.isGlobal());
            return ResponseEntity.ok(new ResponseBody("ok", "Logout was successful"));
        } catch(SecurityException ex){
            return ResponseEntity.ok(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

}
