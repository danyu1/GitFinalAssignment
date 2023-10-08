import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GitTest {

    private static final String TEST_INPUT_FILE = "test_input.txt";
    private static final String TEST_INDEX_FOLDER = "index";
    private static final String TEST_OBJECTS_FOLDER = "objects";
    private static final String TEST_TREE_FILE = "tree";

    @BeforeAll
    static void setUpBeforeClass() throws Exception {

        Utils.writeStringToFile("junit_example_file_data.txt", "test file contents");
        // Utils.deleteFile("Index.txt");
        // Utils.deleteFile("Objects");
        // Utils.deleteFile("Tree");
        // Utils.deleteFile(TEST_TREE_FILE);

    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        // Utils.deleteFile("junit_example_file_data.txt");
        // Utils.deleteFile("Index.txt");
        // Utils.deleteDirectory("Objects");
        // Utils.deleteFile("Tree");
        Utils.deleteFile("test_input.txt");
        Utils.deleteFile("Index.txt");
        Utils.deleteDirectory("Objects");
        Utils.deleteFile(TEST_TREE_FILE);
    }

    @Test
    @DisplayName("Test if creating a Blob works")
    void testBlob() throws Exception {
        // Create a temporary test input file
        createTestInputFile(TEST_INPUT_FILE);

        try {
            Blob.createBlob(TEST_INPUT_FILE);
            String generatedHash = Index.getBlobHash(TEST_INPUT_FILE);

            // Verify that the generated hash is not null
            assertNotNull(generatedHash);

            // Verify that the generated hash file exists in the "objects" directory
            File hashFile = new File("./objects/" + generatedHash);
            assertTrue(hashFile.exists());

            // Clean up the generated hash file
            hashFile.delete();
        } finally {
            // Clean up the test input file
            deleteTestInputFile(TEST_INPUT_FILE);
        }
    }

    // Utility method to create a test input file
    private void createTestInputFile(String filename) throws IOException {
        File TEST_FILE = new File(filename);
        TEST_FILE.createNewFile();
        Files.write(Paths.get(filename), "This is a test.".getBytes());
    }

    // Utility method to delete a test input file
    private void deleteTestInputFile(String filename) {
        File file = new File(Paths.get(filename).toString());
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    @DisplayName("Test if Initializing works - index and objects folders need to be created")
    void testInit() throws Exception {
        // Ensure that the test index and objects folders don't exist before calling
        // init
        deleteTestFolder(TEST_INDEX_FOLDER);
        deleteTestFolder(TEST_OBJECTS_FOLDER);

        Index index = new Index();

        // Check if the index and objects folders were created
        assertTrue(Files.exists(Paths.get(TEST_INDEX_FOLDER)));
        assertTrue(folderExists(TEST_OBJECTS_FOLDER));

        // Clean up by deleting the test folders
        deleteTestFolder(TEST_INDEX_FOLDER);
        deleteTestFolder(TEST_OBJECTS_FOLDER);
    }

    // Utility method to check if a folder exists
    private boolean folderExists(String folderName) {
        File folder = new File(Paths.get(folderName).toString());
        return folder.exists() && folder.isDirectory();
    }

    // Utility method to delete a test folder
    private void deleteTestFolder(String folderName) {
        File folder = new File(Paths.get(folderName).toString());
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            folder.delete();
        }
    }

    @Test
    @DisplayName("Test if adding a Blob works - index gets updated and objectfolder adds the blob")

    void testAdd() throws Exception {
        Index index = new Index();

        // Create a temporary test input file
        createTestInputFile(TEST_INPUT_FILE);

        try {
            // Add a blob to the index
            index.add(TEST_INPUT_FILE);

            // Check if the index has been updated with the blob
            assertTrue(index.containsBlob(TEST_INPUT_FILE));

            // Check if the blob file exists in the "objects" directory
            File blobFile = new File("./objects/" + Index.getBlobHash(TEST_INPUT_FILE));
            assertTrue(blobFile.exists());
        } finally {
            // Clean up the test input file and the added blob
            // deleteTestInputFile(TEST_INPUT_FILE);
            index.remove(TEST_INPUT_FILE);
        }
    }

    @Test
    @DisplayName("Test if removing a Blob works - index gets updated and objectfolder removes the blob")

    void testRemove() throws Exception {
        Index index = new Index();

        // Create a temporary test input file and add it to the index
        createTestInputFile(TEST_INPUT_FILE);
        index.add(TEST_INPUT_FILE);

        // Check if the blob is initially in the index
        assertTrue(index.containsBlob(TEST_INPUT_FILE));

        try {
            // Remove the blob from the index
            index.remove(TEST_INPUT_FILE);

            // Check if the index has been updated and no longer contains the blob
            assertFalse(index.containsBlob(TEST_INPUT_FILE));

            // Check if the blob file has been removed from the "objects" directory
            File blobFile = new File("./objects/" + Index.getBlobHash(TEST_INPUT_FILE));
            assertFalse(blobFile.exists());
        } finally {
            // Clean up the test input file
            deleteTestInputFile(TEST_INPUT_FILE);
        }
    }

    @Test
    @DisplayName("Test Tree Class: Adding, Removing Entries, and Generating Blob")
    void testTreeClass() throws Exception {
        Tree tree = new Tree();

        // Add entries to the tree
        tree.add("testFile1.txt");
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");

        // Generate and save the tree blob
        tree.generateBlob();

        // Check if the tree blob file exists in the 'objects' folder
        File treeBlobFile = new File(Paths.get(Paths.get("objects").toString(), tree.getTreeSha()).toString());
        assertTrue(treeBlobFile.exists());

        // Read the contents of the tree blob file
        List<String> blobLines = Files.readAllLines(treeBlobFile.toPath());

        // Check if the blob contains the expected entries
        assertTrue(blobLines.contains("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : testFile1.txt"));
        assertTrue(blobLines.contains("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b"));

        // Remove an entry and generate the updated blob
        tree.remove("testFile1.txt");
        tree.generateBlob();

        // Check if the removed entry is no longer present in the blob
        blobLines = Files.readAllLines(treeBlobFile.toPath());
        assertTrue(blobLines.contains("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : testFile1.txt"));

        // Clean up the tree blob file
        treeBlobFile.delete();
    }

    @Test
    public void testCommit1() throws Exception {
        File testFile1 = new File("testFile1.txt");
        testFile1.createNewFile();
        Files.write(Paths.get("testFile1.txt"), "test commit content 1".getBytes());
        File testFile2 = new File("testFile2.txt");
        testFile2.createNewFile();
        Files.write(Paths.get("testFile2.txt"), "test commit content 2".getBytes());
        Commit c1 = new Commit("Paco", "initial commit");
        c1.tree.add(testFile1.getName());
        c1.tree.add(testFile2.getName());
        Path commitFile = Paths.get("objects", c1.generateSha1());
        // the sha1 exists
        assertTrue(Files.exists(commitFile));
        File f = new File(commitFile.toString());
        BufferedReader br = new BufferedReader(new FileReader(f));
        br.readLine();
        // next sha should be blank
        assertEquals("", br.readLine());
        // prev sha should be blank
        assertEquals("", br.readLine());
        br.close();

        Files.delete(Paths.get("testFile1.txt"));
        Files.delete(Paths.get("testFile2.txt"));
    }

    @Test
    public void testCommit2() throws Exception {
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

        File folder1 = new File("folder1");
        folder1.mkdir();
        File subfile = new File(folder1.getPath(), "subfile.txt");
        subfile.createNewFile();

        Commit c1 = new Commit("Paco", "initial commit");
        c1.tree.add(testFile1.getName());
        c1.tree.add(testFile2.getName());

        Commit c2 = new Commit("Paco", "second commit");
        c2.tree.add(testFile3.getName());
        c2.tree.add(testFile4.getName());
        String directorySha = c2.tree.addDirectory(folder1.getName());

        Path commitFile1 = Paths.get("objects", c1.generateSha1());
        // the sha1 exists
        assertTrue(Files.exists(commitFile1));
        File f = new File(commitFile1.toString());
        BufferedReader br = new BufferedReader(new FileReader(f));
        br.readLine();
        // next sha should be blank
        assertEquals("", br.readLine());
        // prev sha should be blank
        assertEquals("", br.readLine());
        br.close();

        Path commitFile2 = Paths.get("objects", c2.generateSha1());
        // the sha1 exists
        assertTrue(Files.exists(commitFile2));
        File f2 = new File(commitFile2.toString());
        BufferedReader br2 = new BufferedReader(new FileReader(f2));
        br2.readLine();
        // next sha should be blank
        assertEquals("", br2.readLine());
        // prev sha should be blank
        assertEquals("", br2.readLine());
        br2.close();
        // the object created by the add directory method should have this sha
        assertEquals("e88bd5b6cf00b8369f37cd55ab53d551f0b7e9ec", directorySha);

        Files.delete(Paths.get("testFile1.txt"));
        Files.delete(Paths.get("testFile2.txt"));
        Files.delete(Paths.get("testFile3.txt"));
        Files.delete(Paths.get("testFile4.txt"));
        Files.delete(Paths.get(Paths.get(folder1.getName()).toString(), subfile.getName()));
        Files.delete(Paths.get(folder1.getName()));
    }

    @Test
    public void testCommit3() {

    }
}