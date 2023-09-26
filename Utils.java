import java.io.*;

public class Utils {
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\BlobandIndexRonanUpdated\\";

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

}