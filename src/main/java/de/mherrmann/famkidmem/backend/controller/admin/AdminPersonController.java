package de.mherrmann.famkidmem.backend.controller.admin;

import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.admin.*;
import de.mherrmann.famkidmem.backend.service.admin.AdminPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/person")
public class AdminPersonController {

    private final AdminPersonService adminPersonService;

    @Autowired
    public AdminPersonController(AdminPersonService adminPersonService) {
        this.adminPersonService = adminPersonService;
    }

    @PostMapping(value = "/add")
    public ResponseEntity<ResponseBody> addPerson(@RequestBody RequestBodyAddPerson addPersonRequest){
        try {
            adminPersonService.addPerson(addPersonRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully added person: " + addPersonRequest.getCommonName()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

}
