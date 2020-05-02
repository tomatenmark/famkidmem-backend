package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.admin.RequestBodyAddUser;
import de.mherrmann.famkidmem.backend.exception.MissingValueException;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.exception.UserNotFoundException;
import de.mherrmann.famkidmem.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping(value = "/add-user")
    public ResponseEntity<ResponseBody> addUser(@RequestBody RequestBodyAddUser addUserRequest){
        try {
            adminService.addUser(addUserRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully added user: " + addUserRequest.getUsername()));
        } catch (UserNotFoundException | SecurityException | MissingValueException ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

}
