package de.mherrmann.famkidmem.backend.controller.admin;

import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyDeleteUser;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyResetPassword;
import de.mherrmann.famkidmem.backend.body.admin.ResponseBodyGetUsers;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.exception.AddUserException;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
import de.mherrmann.famkidmem.backend.service.admin.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        } catch (AddUserException | EntityNotFoundException | SecurityException ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<ResponseBody> deleteUser(@RequestBody RequestBodyDeleteUser deleteUserRequest){
        try {
            adminUserService.deleteUser(deleteUserRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully deleted user: " + deleteUserRequest.getUsername()));
        } catch (SecurityException | EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @GetMapping(value = "/get/{accessToken}")
    public ResponseEntity<ResponseBodyGetUsers> getUser(@PathVariable String accessToken){
        try {
            ResponseBodyGetUsers usersResponse = adminUserService.getUsers(accessToken);
            return ResponseEntity.ok(usersResponse);
        } catch (SecurityException | EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ResponseBodyGetUsers(ex));
        }
    }

    @PostMapping(value = "/reset")
    public ResponseEntity<ResponseBody> resetPassword(@RequestBody RequestBodyResetPassword resetPasswordRequest){
        try {
            adminUserService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully reset password for user: " + resetPasswordRequest.getUsername()));
        } catch (SecurityException | EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }
}
