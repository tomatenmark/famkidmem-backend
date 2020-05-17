package de.mherrmann.famkidmem.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static String filesDir = "./files/";

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    private void defineFilesDir(String[] args){
        for(String arg : args){
            if(arg.startsWith("--files_dir=")){
                filesDir = arg.substring(arg.indexOf('=')+1);
                break;
            }
        }
    }
}
