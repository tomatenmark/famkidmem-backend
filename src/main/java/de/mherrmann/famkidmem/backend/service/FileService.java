package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.entity.FileEntity;
import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileEntity addFile(Key key, String filename){
        FileEntity file = new FileEntity(key, filename);
        return fileRepository.save(file);
    }

    public void delete(FileEntity file){
        fileRepository.delete(file);
    }
}
