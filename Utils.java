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
        Files.write(Paths.get("tree"), "".getBytes());
        Files.write(Paths.get("commit"), "".getBytes());
        Files.write(Paths.get("head"), "".getBytes());
    }

    public static void createFile(String fileName) throws Exception {
        (new File(fileName)).createNewFile();
    }

    public static void createAllTestFile() throws Exception {
        File testFile1 = new File("testFile1.txt");
        testFile1.createNewFile();
        Files.write(Paths.get("testFile1.txt"), "test commit content 1".getBytes());
        File testFile2 = new File("testFile2.txt");
        testFile2.createNewFile();
        Files.write(Paths.get("testFile2.txt"), "test commit content 2".getBytes());
        File testFile3 = new File("testFile3.txt");
        testFile3.createNewFile();
        Files.write(Paths.get("testFile3.txt"), "test commit content 3".getBytes());
        File testFile4 = new File("testFile4.txt");
        testFile4.createNewFile();
        Files.write(Paths.get("testFile4.txt"), "test commit content 4".getBytes());
        File testFile5 = new File("testFile5.txt");
        testFile5.createNewFile();
        Files.write(Paths.get("testFile5.txt"), "test commit content 5".getBytes());
        File testFile6 = new File("testFile6.txt");
        testFile6.createNewFile();
        Files.write(Paths.get("testFile6.txt"), "test commit content 6".getBytes());
        File testFile7 = new File("testFile7.txt");
        testFile7.createNewFile();
        Files.write(Paths.get("testFile7.txt"), "test commit content 7".getBytes());
        File testFile8 = new File("testFile8.txt");
        testFile8.createNewFile();
        Files.write(Paths.get("testFile8.txt"), "test commit content 8".getBytes());

        File folder1 = new File("folder1");
        folder1.mkdir();
        File subfile = new File(folder1.getPath(), "subfile.txt");
        subfile.createNewFile();

        File folder2 = new File("folder2");
        folder2.mkdir();
        File subfile2 = new File(folder2.getPath(), "subfile2.txt");
        subfile2.createNewFile();

        File objects = new File("objects");
        objects.mkdir();

    }

    public static void deleteAllTestFile() throws Exception {
        Files.delete(Paths.get("testFile1.txt"));
        Files.delete(Paths.get("testFile2.txt"));
        Files.delete(Paths.get("testFile3.txt"));
        Files.delete(Paths.get("testFile4.txt"));
        Files.delete(Paths.get("testFile5.txt"));
        Files.delete(Paths.get("testFile6.txt"));
        Files.delete(Paths.get("testFile7.txt"));
        Files.delete(Paths.get("testFile8.txt"));

        File folder1 = new File("folder1");
        File subfile = new File(folder1.getPath(), "subfile.txt");

        File folder2 = new File("folder2");
        File subfile2 = new File(folder2.getPath(), "subfile2.txt");

        Files.delete(Paths.get(Paths.get(folder1.getName()).toString(), subfile.getName()));
        Files.delete(Paths.get(Paths.get(folder2.getName()).toString(), subfile2.getName()));
        Files.delete(Paths.get(folder1.getName()));
        Files.delete(Paths.get(folder2.getName()));
    }

}