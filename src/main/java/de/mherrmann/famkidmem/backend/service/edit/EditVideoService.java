package de.mherrmann.famkidmem.backend.service.edit;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyAddVideo;
import de.mherrmann.famkidmem.backend.body.edit.RequestBodyUpdateVideo;
import de.mherrmann.famkidmem.backend.entity.*;
import de.mherrmann.famkidmem.backend.exception.AddEntityException;
import de.mherrmann.famkidmem.backend.exception.EntityActionException;
import de.mherrmann.famkidmem.backend.exception.EntityNotFoundException;
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
import java.util.Optional;

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
        validateNew(addVideoRequest);
        add(addVideoRequest);
    }

    public void updateVideo(RequestBodyUpdateVideo updateVideoRequest) throws EntityActionException, EntityNotFoundException {
        Video video = getVideo(updateVideoRequest.getDesignator());
        validateUpdate(updateVideoRequest);
        update(video, updateVideoRequest);
    }

    public void deleteVideo(String designator) throws EntityActionException, EntityNotFoundException {
        Video video = getVideo(designator);
        delete(video);
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

    private void update(Video video, RequestBodyUpdateVideo updateVideoRequest){
        keyEntityService.updateKey(updateVideoRequest.getKey(), updateVideoRequest.getIv(), video.getKey());
        keyEntityService.updateKey(updateVideoRequest.getThumbnailKey(), updateVideoRequest.getThumbnailIv(), video.getThumbnail().getKey());
        video.setTitle(updateVideoRequest.getTitle());
        video.setDescription(updateVideoRequest.getDescription());
        video.setRecordedInCologne(updateVideoRequest.isRecordedInCologne());
        video.setRecordedInGardelgen(updateVideoRequest.isRecordedInGardelgen());
        List<Person> personsBefore = video.getPersons();
        List<Year> yearsBefore = video.getYears();
        video.setYears(getYears(updateVideoRequest.getYears()));
        video.setPersons(getPersons(updateVideoRequest.getPersons()));
        videoRepository.save(video);
        personsBefore.forEach(this::deletePersonIfOrphan);
        yearsBefore.forEach(this::deleteYearIfOrphan);
    }

    private void delete(Video video){
        List<Person> persons = video.getPersons();
        List<Year> years = video.getYears();
        videoRepository.delete(video);
        fileEntityService.delete(video.getThumbnail());
        fileEntityService.delete(video.getM3u8());
        keyEntityService.delete(video.getKey());
        keyEntityService.delete(video.getThumbnail().getKey());
        keyEntityService.delete(video.getM3u8().getKey());
        persons.forEach(this::deletePersonIfOrphan);
        years.forEach(this::deleteYearIfOrphan);
    }

    private void deletePersonIfOrphan(Person person){
        if(!videoRepository.existsByPersonsContains(person)){
            personEntityService.delete(person);
        }
    }

    private void deleteYearIfOrphan(Year year){
        if(!videoRepository.existsByYearsContains(year)){
            yearEntityService.delete(year);
        }
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


    private void validateNew(RequestBodyAddVideo addVideoRequest) throws AddEntityException {
        if(addVideoRequest.getTitle().isEmpty()){
            LOGGER.error("Could not add video. Title can not be empty.");
            throw new AddEntityException("Title can not be empty.");
        }
        if(videoRepository.existsByTitle(addVideoRequest.getTitle())){
            LOGGER.error("Could not add video. Video with same title {} already exists.", addVideoRequest.getTitle());
            throw new AddEntityException("Video with same title already exists.");
        }
        if(missingKeyInfoForNew(addVideoRequest)){
            LOGGER.error("Could not add video. Key info missing.");
            throw new AddEntityException("Key info missing.");
        }
        if(missingFiles(addVideoRequest)){
            LOGGER.error("Could not add video. File(s) missing.");
            throw new AddEntityException("File(s) missing.");
        }
    }

    private void validateUpdate(RequestBodyUpdateVideo updateVideoRequest) throws EntityActionException {
        if(updateVideoRequest.getTitle().isEmpty()){
            LOGGER.error("Could not update video. Title can not be empty.");
            throw new EntityActionException("Title can not be empty.");
        }
        if(!updateVideoRequest.getTitle().equals(updateVideoRequest.getDesignator()) && videoRepository.existsByTitle(updateVideoRequest.getTitle())){
            LOGGER.error("Could not update video. Video with same title {} already exists.", updateVideoRequest.getTitle());
            throw new EntityActionException("Video with same title already exists.");
        }
        if(missingKeyInfoForUpdate(updateVideoRequest)){
            LOGGER.error("Could not update video. Key info missing.");
            throw new EntityActionException("Key info missing.");
        }
    }

    private boolean missingKeyInfoForNew(RequestBodyAddVideo addVideoRequest){
        if(addVideoRequest.getKey().isEmpty()) return true;
        if(addVideoRequest.getIv().isEmpty()) return true;
        if(addVideoRequest.getM3u8Key().isEmpty()) return true;
        if(addVideoRequest.getM3u8Iv().isEmpty()) return true;
        if(addVideoRequest.getThumbnailKey().isEmpty()) return true;
        return (addVideoRequest.getThumbnailIv().isEmpty());
    }

    private boolean missingKeyInfoForUpdate(RequestBodyUpdateVideo updateVideoRequest){
        if(updateVideoRequest.getKey().isEmpty()) return true;
        if(updateVideoRequest.getIv().isEmpty()) return true;
        if(updateVideoRequest.getThumbnailKey().isEmpty()) return true;
        return (updateVideoRequest.getThumbnailIv().isEmpty());
    }

    private boolean missingFiles(RequestBodyAddVideo addVideoRequest){
        boolean missingM3u8 =      ! new File(Application.filesDir+addVideoRequest.getM3u8Filename()).exists();
        boolean missingThumbnail = ! new File(Application.filesDir+addVideoRequest.getThumbnailFilename()).exists();
        return missingM3u8 || missingThumbnail;
    }

    private Video getVideo(String designator) throws EntityNotFoundException {
        Optional<Video> videoOptional = videoRepository.findByTitle(designator);
        if(!videoOptional.isPresent()){
            LOGGER.error("Could not update video. Video not found. Designator: {}", designator);
            throw new EntityNotFoundException(Video.class, designator);
        }
        return videoOptional.get();
    }
}
