import java.io.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Index {

    private HashMap<String, String> files = new HashMap<>();
    private static String path = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\BlobandIndexRonanUpdated";

    public Index() throws IOException {
        init();
    }

    public static void init() throws IOException {
        // init should not create the index file inside the objects folder
        java.nio.file.Path folderPath = Paths.get(path + "\\objects");
        if (!Files.exists(folderPath)) {
            Files.createDirectory(folderPath);
        }

        Path indexPath = Paths.get(path + "\\index");
        if (!Files.exists(indexPath)) {
            Files.createFile(indexPath);
        }
    }

    public void addBlob(String fileName) throws IOException, NoSuchAlgorithmException {
        String hashName = Blob.blob(fileName);

        files.put(fileName, hashName);

        Path indexPath = Paths.get(path + File.separator + "index");
        try (BufferedWriter writer = Files.newBufferedWriter(indexPath, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            writer.write("blob : " + fileName + " : " + hashName);
            writer.newLine();
        }
    }

    public void removeBlob(String fileName) throws IOException {
        files.remove(fileName);

        Path indexPath = Paths.get(path + File.separator + "index");
        List<String> lines = Files.readAllLines(indexPath);
        lines.removeIf(line -> line.startsWith(fileName + " : "));
        Files.write(indexPath, lines);
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Index index = new Index();
        index.addBlob("test.txt");
        index.addBlob("test2.txt");
        index.removeBlob("test.txt");
    }

    public boolean containsBlob(String fileName) {
        return files.containsKey(fileName);
    }

    public String getBlobHash(String fileName) {
        return files.get(fileName);
    }
}
