package de.mherrmann.famkidmem.backend.controller.admin;

import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.exception.AddUserException;
import de.mherrmann.famkidmem.backend.exception.UserNotFoundException;
import de.mherrmann.famkidmem.backend.service.admin.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Autowired
    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping(value = "/add")
    public ResponseEntity<ResponseBody> addUser(@RequestBody RequestBodyAddUser addUserRequest){
        try {
            adminUserService.addUser(addUserRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully added user: " + addUserRequest.getUsername()));
        } catch (AddUserException | SecurityException ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @PostMapping(value = "/reset")
    public ResponseEntity<ResponseBody> addUser(@RequestBody RequestBodyResetPassword resetPasswordRequest){
        try {
            adminUserService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully reset password for user: " + resetPasswordRequest.getUsername()));
        } catch (SecurityException | UserNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }
}
