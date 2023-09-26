import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

public class IndexTest {
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\BlobandIndexRonanUpdated";
    static Index index;

    @BeforeAll
    static void setUpBefore() throws Exception {
        index = new Index();
        File file1 = new File(pathToWorkSpace + "\\junit_example_test1.txt");
        File file2 = new File(pathToWorkSpace + "\\junit_example_test2.txt");
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
    void testAddBlob() throws Exception {
        index.addBlob("junit_example_test1.txt");
        index.addBlob("junit_example_test2.txt");
        File indexPath = new File(pathToWorkSpace + "//index");
        BufferedReader br = new BufferedReader(new FileReader(indexPath));
        // assert that the correct content was writting to the index file if any at all
        assertEquals("blob : junit_example_test1.txt : f5cda28ce12d468c64a6a2f2224971f894442f1b", br.readLine());
        assertEquals("blob : junit_example_test2.txt : 50d4b41eed4faffe212d8cf6ec89d7889dfeff9e", br.readLine());
        br.close();

        // delete the blobs in the objects folder
        Path tree1 = Paths.get(pathToWorkSpace + "\\objects", "f5cda28ce12d468c64a6a2f2224971f894442f1b");
        Path tree2 = Paths.get(pathToWorkSpace + "\\objects", "50d4b41eed4faffe212d8cf6ec89d7889dfeff9e");
        Files.delete(tree1);
        Files.delete(tree2);
    }

    @Test
    void testContainsBlob() throws Exception {
        assertFalse(index.containsBlob("junit_example_test1.txt"));
        assertFalse(index.containsBlob("junit_example_test2.txt"));
    }

    @Test
    void testGetBlobHash() throws Exception {
        index.addBlob("junit_example_test1.txt");
        index.addBlob("junit_example_test2.txt");
        // assert that the getBlobHash method returns the expected SHA1 hash
        assertEquals("f5cda28ce12d468c64a6a2f2224971f894442f1b", index.getBlobHash("junit_example_test1.txt"));
        assertEquals("50d4b41eed4faffe212d8cf6ec89d7889dfeff9e", index.getBlobHash("junit_example_test2.txt"));

        // delete the blobs in the objects folder
        Path tree1 = Paths.get(pathToWorkSpace + "\\objects", "f5cda28ce12d468c64a6a2f2224971f894442f1b");
        Path tree2 = Paths.get(pathToWorkSpace + "\\objects", "50d4b41eed4faffe212d8cf6ec89d7889dfeff9e");
        Files.delete(tree1);
        Files.delete(tree2);
    }

    @Test
    void testInit() throws Exception {
        Path indexPath = Paths.get(pathToWorkSpace);
        Path objectsPath = Paths.get(pathToWorkSpace + "\\objects");
        // assert that the objects directory and index file are created in their proper
        // locations
        assertTrue(Files.exists(indexPath));
        assertTrue(Files.exists(objectsPath));
    }

    @Test
    void testRemoveBlob() throws Exception {
        index.removeBlob("junit_example_test1.txt");
        index.removeBlob("junit_example_test2.txt");
        assertFalse(index.containsBlob("junit_example_test1.txt"));
        assertFalse(index.containsBlob("junit_example_test2.txt"));
    }
}
