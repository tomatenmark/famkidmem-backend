package de.mherrmann.famkidmem.backend.controller.ccms;

import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.backend.service.edit.EditVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/ccms/edit/video")
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
    public ResponseEntity<ResponseBody> updateVideo(@RequestBody RequestBodyUpdateVideo updateVideoRequest){
        try {
            editVideoService.updateVideo(updateVideoRequest);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully updated video: " + updateVideoRequest.getTitle()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }

    @DeleteMapping(value = "/delete/{designator}")
    public ResponseEntity<ResponseBody> deleteVideo(@PathVariable String designator){
        try {
            editVideoService.deleteVideo(designator);
            return ResponseEntity.ok(new ResponseBody("ok", "Successfully removed video: " + designator));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ResponseBody("error", ex.getMessage(), ex));
        }
    }
}
