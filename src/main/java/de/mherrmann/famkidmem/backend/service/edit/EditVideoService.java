package de.mherrmann.famkidmem.backend.service.edit;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.entity.*;
import de.mherrmann.famkidmem.backend.exception.AddEntityException;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import de.mherrmann.famkidmem.backend.service.subentity.FileEntityService;
import de.mherrmann.famkidmem.backend.service.subentity.KeyEntityService;
import de.mherrmann.famkidmem.backend.service.subentity.PersonEntityService;
import de.mherrmann.famkidmem.backend.service.subentity.YearEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class EditVideoService {

    private final VideoRepository videoRepository;
    private final KeyEntityService keyEntityService;
    private final FileEntityService fileEntityService;
    private final PersonEntityService personEntityService;
    private final YearEntityService yearEntityService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EditVideoService.class);

    @Autowired
    public EditVideoService(
            VideoRepository videoRepository, KeyEntityService keyEntityService, FileEntityService fileEntityService,
            PersonEntityService personEntityService, YearEntityService yearEntityService) {
        this.videoRepository = videoRepository;
        this.keyEntityService = keyEntityService;
        this.fileEntityService = fileEntityService;
        this.personEntityService = personEntityService;
        this.yearEntityService = yearEntityService;
    }

    public void addVideo(RequestBodyAddVideo addVideoRequest) throws AddEntityException {
        validate(addVideoRequest);
        add(addVideoRequest);
    }

    private void add(RequestBodyAddVideo addVideoRequest){
        Key key = keyEntityService.addKey(addVideoRequest.getKey(), addVideoRequest.getIv());
        Key thumbnailKey = keyEntityService.addKey(addVideoRequest.getThumbnailKey(), addVideoRequest.getThumbnailIv());
        Key m3u8Key = keyEntityService.addKey(addVideoRequest.getM3u8Key(), addVideoRequest.getM3u8Iv());
        FileEntity thumbnail = fileEntityService.addFile(thumbnailKey, addVideoRequest.getThumbnailFilename());
        FileEntity m3u8 = fileEntityService.addFile(m3u8Key, addVideoRequest.getM3u8Filename());
        List<Person> persons = getPersons(addVideoRequest.getPersons());
        List<Year> years = getYears(addVideoRequest.getYears());
        Video video = new Video(
                addVideoRequest.getTitle(),
                addVideoRequest.getDescription(),
                addVideoRequest.getDurationInSeconds(),
                addVideoRequest.isRecordedInCologne(),
                addVideoRequest.isRecordedInGardelgen(),
                years,
                persons,
                key,
                thumbnail,
                m3u8
        );
        videoRepository.save(video);
    }

    private List<Person> getPersons(List<String> names){
        List<Person> persons = new ArrayList<>();
        for(String name : names){
            persons.add(personEntityService.getPerson(name));
        }
        return persons;
    }

    private List<Year> getYears(List<Integer> values){
        List<Year> years = new ArrayList<>();
        for(Integer value : values){
            years.add(yearEntityService.getYear(value));
        }
        return years;
    }


    private void validate(RequestBodyAddVideo addVideoRequest) throws AddEntityException {
        if(addVideoRequest.getTitle().isEmpty()){
            LOGGER.error("Could not add video. Title can not be empty.");
            throw new AddEntityException("Title can not be empty.");
        }
        if(videoRepository.existsByTitle(addVideoRequest.getTitle())){
            LOGGER.error("Could not add video. Video with same title {} already exists.", addVideoRequest.getTitle());
            throw new AddEntityException("Video with same title already exists.");
        }
        if(missingKeyInfo(addVideoRequest)){
            LOGGER.error("Could not add video. Key info missing.");
            throw new AddEntityException("Key info missing.");
        }
        if(missingFile(addVideoRequest)){
            LOGGER.error("Could not add video. File(s) missing.");
            throw new AddEntityException("File(s) missing.");
        }
    }

    private boolean missingKeyInfo(RequestBodyAddVideo addVideoRequest){
        if(addVideoRequest.getKey().isEmpty()) return true;
        if(addVideoRequest.getIv().isEmpty()) return true;
        if(addVideoRequest.getM3u8Key().isEmpty()) return true;
        if(addVideoRequest.getM3u8Iv().isEmpty()) return true;
        if(addVideoRequest.getThumbnailKey().isEmpty()) return true;
        return (addVideoRequest.getThumbnailIv().isEmpty());
    }

    private boolean missingFile(RequestBodyAddVideo addVideoRequest){
        boolean missingM3u8 =      ! new File(Application.filesDir+addVideoRequest.getM3u8Filename()).exists();
        boolean missingThumbnail = ! new File(Application.filesDir+addVideoRequest.getThumbnailFilename()).exists();
        return missingM3u8 || missingThumbnail;
    }
}
