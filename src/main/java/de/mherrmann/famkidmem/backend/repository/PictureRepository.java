package de.mherrmann.famkidmem.backend.repository;

import de.mherrmann.famkidmem.backend.entity.Picture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureRepository extends CrudRepository<Picture, String> {

}
