package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.service.VideoService;
import de.mherrmann.famkidmem.backend.service.ccms.EditVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class VideoTsController {

    private final VideoService videoService;
    private final EditVideoService editVideoService;

    @Autowired
    public VideoTsController(VideoService videoService, EditVideoService editVideoService) {
        this.videoService = videoService;
        this.editVideoService = editVideoService;
    }



    @ResponseBody
    @GetMapping(value = "/api/ts/{accessToken}/{filename}")
    public ResponseEntity getTsAuthorizedByLogin(@PathVariable String accessToken, @PathVariable String filename) {
        try {
            return videoService.getTsFile(accessToken, filename);
        } catch(Exception ex){
            return ResponseEntity.badRequest().body(new de.mherrmann.famkidmem.backend.body.ResponseBody("error", ex.getMessage()));
        }
    }

    @ResponseBody
    @GetMapping(value = "/ccms/video/ts/{filename}")
    public ResponseEntity getTsAuthorizedByApiKey(@PathVariable String filename) {
        try {
            return editVideoService.getTsFile(filename);
        } catch(Exception ex){
            return ResponseEntity.badRequest().body(new de.mherrmann.famkidmem.backend.body.ResponseBody("error", ex.getMessage()));
        }
    }
}
