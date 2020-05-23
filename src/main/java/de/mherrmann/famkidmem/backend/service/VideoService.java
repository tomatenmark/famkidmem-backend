package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.body.content.ResponseBodyContentIndex;
import de.mherrmann.famkidmem.backend.entity.Person;
import de.mherrmann.famkidmem.backend.entity.UserEntity;
import de.mherrmann.famkidmem.backend.entity.Video;
import de.mherrmann.famkidmem.backend.entity.Year;
import de.mherrmann.famkidmem.backend.repository.PersonRepository;
import de.mherrmann.famkidmem.backend.repository.VideoRepository;
import de.mherrmann.famkidmem.backend.repository.YearRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final PersonRepository personRepository;
    private final YearRepository yearRepository;
    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    public VideoService(VideoRepository videoRepository, PersonRepository personRepository, YearRepository yearRepository, UserService userService) {
        this.videoRepository = videoRepository;
        this.personRepository = personRepository;
        this.yearRepository = yearRepository;
        this.userService = userService;
    }

    public ResponseBodyContentIndex getIndex(String accessToken) throws SecurityException {
        UserEntity user = userService.getUser(accessToken, "get video index");
        LOGGER.info("Successfully got video index. AccessToken: {}", accessToken);
        return new ResponseBodyContentIndex(user.getMasterKey(), getVideos(), getPersons(), getYears());
    }

    private List<Video> getVideos(){
        List<Video> videos = new ArrayList<>();
        Iterable<Video> videoIterable = videoRepository.findAll();
        videoIterable.forEach(videos::add);
        return videos;
    }

    private List<String> getPersons(){
        List<String> persons = new ArrayList<>();
        Iterable<Person> personsIterable = personRepository.findAll();
        personsIterable.forEach(e->persons.add(e.getName()));
        return persons;
    }

    private List<Integer> getYears(){
        List<Integer> years = new ArrayList<>();
        Iterable<Year> yearsIterable = yearRepository.findAll();
        yearsIterable.forEach(e->years.add(e.getValue()));
        return years;
    }
}
