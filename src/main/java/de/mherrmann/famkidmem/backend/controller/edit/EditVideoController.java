package de.mherrmann.famkidmem.backend.controller.edit;

import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.backend.service.edit.EditVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/edit/video")
public class EditVideoController {

    private final EditVideoService editVideoService;

    @Autowired
    public EditVideoController(EditVideoService editVideoService) {
        this.editVideoService = editVideoService;
    }

    @PostMapping(value = "/add")
    public ResponseEntity<ResponseBody> addVideo(@RequestBody RequestBodyAddVideo addVideoRequest){
        try {
            editVideoService.addVideo(addVideoRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully added video: " + addVideoRequest.getTitle()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @PostMapping(value = "/update")
    public ResponseEntity<ResponseBody> addVideo(@RequestBody RequestBodyUpdateVideo updateVideoRequest){
        try {
            editVideoService.updateVideo(updateVideoRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully updated video: " + updateVideoRequest.getTitle()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }
}
