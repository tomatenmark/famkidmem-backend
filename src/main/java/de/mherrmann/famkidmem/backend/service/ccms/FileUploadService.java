package de.mherrmann.famkidmem.backend.service.ccms;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.exception.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileUploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadService.class);

    public void store(MultipartFile file) throws FileUploadException {
        doChecks(file);
        storeFile(file);
        LOGGER.info("Successfully saved file {}", file.getName());
    }

    private void doChecks(MultipartFile file) throws FileUploadException {
        if(file.getOriginalFilename().isEmpty()){
            LOGGER.error("Can not save file with empty name.");
            throw new FileUploadException("Can not save file with empty name.");
        }
        if(file.isEmpty()){
            LOGGER.error("Can not save empty file");
            throw new FileUploadException("Can not save empty file.");
        }
    }

    private void storeFile(MultipartFile multipartFile) throws FileUploadException {
        String fileName = multipartFile.getOriginalFilename().replaceAll("\\.+", ".");
        try {
            File destinationFile = new File(Application.filesDir + fileName);
            destinationFile.createNewFile();
            multipartFile.transferTo(destinationFile);
        } catch(IOException ex){
            LOGGER.error("Could not save file {}. I/O Error", fileName, ex);
            throw new FileUploadException("Could not save file. I/O Error");
        }

    }
}
