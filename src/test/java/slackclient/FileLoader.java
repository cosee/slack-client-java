package slackclient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FileLoader {

    public static String loadFile(String path) {
        return asString(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
    }

    private static String asString(InputStream inputStream) {
        return new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\Z").next();
    }
}
