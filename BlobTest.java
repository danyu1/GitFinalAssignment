import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BlobTest {
    // important note: write (String hashed, StringBuilder inside) method was not
    // tested because its functionality was tested in testBlob (). write method is
    // only a helper method that creates the hashed file with its contents in the
    // objects folder (testBlob calls write method within itself)
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\";

    @BeforeAll
    static void setUpBefore() throws Exception {
        Index index = new Index();
        File file1 = new File(pathToWorkSpace + "junit_example_test1.txt");
        File file2 = new File(pathToWorkSpace + "junit_example_test2.txt");
        file1.createNewFile();
        file2.createNewFile();
        PrintWriter pw1 = new PrintWriter(file1);
        PrintWriter pw2 = new PrintWriter(file2);
        pw1.write("some content for file 1");
        pw2.write("some content for file 2");
        pw1.close();
        pw2.close();
    }

    @AfterAll
    static void tearDownAfter() throws Exception {
        Utils.deleteFile("junit_example_test1.txt");
        Utils.deleteFile("junit_example_test2.txt");
        Utils.deleteFile("index");
        Utils.deleteDirectory("objects");
    }

    @Test
    void testBlob() throws Exception {
        Blob.blob("junit_example_test1.txt");
        Blob.blob("junit_example_test2.txt");

        Path tree1 = Paths.get(pathToWorkSpace + "objects", "f5cda28ce12d468c64a6a2f2224971f894442f1b");
        Path tree2 = Paths.get(pathToWorkSpace + "objects", "50d4b41eed4faffe212d8cf6ec89d7889dfeff9e");

        // assert that the blob was created in the objects folder with the appropriate
        // hash name
        assertTrue(Files.exists(tree1));
        assertTrue(Files.exists(tree2));
        // assert that the blob created has the same contents as the original text file
        File file1 = new File(pathToWorkSpace + "objects/f5cda28ce12d468c64a6a2f2224971f894442f1b");
        BufferedReader br1 = new BufferedReader(new FileReader(file1));
        File file2 = new File(pathToWorkSpace + "objects/50d4b41eed4faffe212d8cf6ec89d7889dfeff9e");
        BufferedReader br2 = new BufferedReader(new FileReader(file2));
        assertEquals("some content for file 1", br1.readLine());
        assertEquals("some content for file 2", br2.readLine());
        br1.close();
        br2.close();
        Files.delete(tree1);
        Files.delete(tree2);
    }

    @Test
    void testGenerateSHA() throws Exception {
        // assert that generateSHA method returns the correct SHA hash
        assertEquals("f5cda28ce12d468c64a6a2f2224971f894442f1b", Blob.generateSHA("some content for file 1"));
        assertEquals("50d4b41eed4faffe212d8cf6ec89d7889dfeff9e", Blob.generateSHA("some content for file 2"));
    }
}
