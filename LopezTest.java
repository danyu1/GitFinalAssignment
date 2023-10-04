import java.io.File;
import java.nio.file.Files;

public class LopezTest {
    public static String testFolder = "testFolder";
    public static  String testFolder1 = "testFolder/innerFolder";


    public static void main(String[] args) throws Exception {
        generateFilesAndFolders();
       
        Tree tree = new Tree();
        String treeSHA = tree.addDirectory(testFolder);

        System.out.println("Tree SHA: " + treeSHA);

    }
    
    public static void generateFilesAndFolders() throws Exception {
        File folder = new File(testFolder);
        folder.mkdir();

        File testFile = new File("testFolder/testFile.txt");
        File testFile2 = new File("testFolder/testFile2.txt");
        File testFile3 = new File("testFolder/testFile3.txt");

        try {
            testFile.createNewFile();
            testFile2.createNewFile();
            testFile3.createNewFile();
            writeToFile(testFile, "This is a test file 1");
            writeToFile(testFile2, "This is a test file 2");
            writeToFile(testFile3, "This is a test file 3");
        } catch (Exception e) {
            System.out.println("Error creating file");
        }

        File folder1 = new File(testFolder1);
        folder1.mkdir();

        File testFile4 = new File("testFolder/innerFolder/testFile4.txt");
        File testFile5 = new File("testFolder/innerFolder/testFile5.txt");
        File testFile6 = new File("testFolder/innerFolder/testFile6.txt");
        try {
            testFile4.createNewFile();
            testFile5.createNewFile();
            testFile6.createNewFile();

            writeToFile(testFile4, "This is a test file 4");
            writeToFile(testFile5, "This is a test file 5");
            writeToFile(testFile6, "This is a test file 6");

        } catch (Exception e) {
            System.out.println("Error creating inner files");
        }
    }

       public static void writeToFile(File file, String content) throws Exception {
        Files.write(file.toPath(), content.getBytes());
    }
}
