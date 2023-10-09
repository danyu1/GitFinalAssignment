import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\";

    public static void writeStringToFile(String filename, String str) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(filename);
        pw.print(str);
        pw.close();
    }

    public static String writeFileToString(String f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();
        while (br.ready()) {
            sb.append((char) br.read());
        }
        br.close();
        return sb.toString();
    }

    public static void deleteFile(String f) {
        File file = new File(pathToWorkSpace + f);
        file.delete();
    }

    public static void deleteDirectory(String f) {
        File file = new File(pathToWorkSpace + f);
        file.delete();
    }

    public static void cleanFiles() throws Exception {
        Files.write(Paths.get("index"), "".getBytes());
        Files.write(Paths.get("tree"), "".getBytes());
        Files.write(Paths.get("commit"), "".getBytes());
    }

    public static void createFile(String fileName) throws Exception {
        (new File(fileName)).createNewFile();
    }

}