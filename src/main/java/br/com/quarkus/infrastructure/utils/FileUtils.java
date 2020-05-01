package br.com.quarkus.infrastructure.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class FileUtils {

    private static String getFileNameReaded(String directoryRead, Path path) {
        return directoryRead +"/"+ path.getFileName() + "-" + new Date();
    }

    public static void move(Path path, String directoryRead) throws IOException {
        Files.move(Paths.get(path.toString()), Paths.get(getFileNameReaded(directoryRead, path)));
    }


}
