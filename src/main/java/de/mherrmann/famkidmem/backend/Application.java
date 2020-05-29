package de.mherrmann.famkidmem.backend;

import de.mherrmann.famkidmem.backend.utils.Bcrypt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Application {

    public static String filesDir = "./files/";

    public static void main(String[] args) {
        defineFilesDir(args);
        Bcrypt.hash("preparing Bcrypt");
        SpringApplication.run(Application.class, args);
    }

    private static void defineFilesDir(String[] args){
        for(String arg : args){
            if(arg.startsWith("--files_dir=")){
                filesDir = arg.substring(arg.indexOf('=')+1);
                break;
            }
        }
        checkFilesDirAvailable();
    }

    private static void checkFilesDirAvailable(){
        File file = new File(filesDir);
        if(!file.exists() || !file.isDirectory() || !file.canRead() || !file.canWrite()){
            System.err.println("Fatal ERROR: defined filesDir does not exist, is not a directory or is not accessible! Shutting down");
            System.exit(1);
        }
    }
}
