package de.mherrmann.famkidmem.backend.controller;

import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentIndex;
import de.mherrmann.famkidmem.backend.exception.SecurityException;
import de.mherrmann.famkidmem.backend.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/video")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping(value = "/index/{accessToken}")
    public ResponseEntity<ResponseBodyContentIndex> getIndex(@PathVariable String accessToken) {
        try {
            return ResponseEntity.ok(videoService.getIndex(accessToken));
        } catch(SecurityException ex){
            return ResponseEntity.badRequest().body(new ResponseBodyContentIndex(ex));
        }
    }
}
