package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/ts/")
public class VideoTsController {

    private final VideoService videoService;

    @Autowired
    public VideoTsController(VideoService videoService) {
        this.videoService = videoService;
    }



    @ResponseBody
    @GetMapping(value = "/{accessToken}/{filename}")
    public ResponseEntity getTs(@PathVariable String accessToken, @PathVariable String filename) {
        try {
            return videoService.getTsFile(accessToken, filename);
        } catch(Exception ex){
            return ResponseEntity.badRequest().body(new de.mherrmann.famkidmem.backend.body.ResponseBody("error", ex.getMessage()));
        }
    }
}
