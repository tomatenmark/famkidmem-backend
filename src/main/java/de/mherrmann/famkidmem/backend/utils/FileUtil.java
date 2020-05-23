package de.mherrmann.famkidmem.backend.utils;

import de.mherrmann.famkidmem.backend.Application;
import de.mherrmann.famkidmem.backend.exception.FileNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class FileUtil {

    public String getBase64EncodedContent(String filename) throws FileNotFoundException, IOException {
        File file = new File(Application.filesDir + filename);
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            byte fileData[] = new byte[(int) file.length()];
            imageInFile.read(fileData);
            return Base64.getEncoder().encodeToString(fileData);
        } catch (java.io.FileNotFoundException e) {
            throw new FileNotFoundException(filename);
        } catch (IOException ioe) {
            throw new IOException("There was an error reading the file: " + filename);
        }
    }

}
