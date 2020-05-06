package de.mherrmann.famkidmem.backend.service;

import de.mherrmann.famkidmem.backend.entity.Key;
import de.mherrmann.famkidmem.backend.repository.KeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyService {

    private final KeyRepository keyRepository;

    @Autowired
    public KeyService(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    public Key createNewKey(String key, String iv){
        Key keyEntity = new Key(key, iv);
        return keyRepository.save(keyEntity);
    }

}
