import static org.junit.Assert.assertNotEquals;
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
import java.util.ArrayList;
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

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        Utils.writeStringToFile("junit_example_file_data.txt", "test file contents");
        Index index = new Index();
        Utils.createFile("commit");
        Utils.createFile(TEST_TREE_FILE);
        Utils.createFile("head");
        // create all the test files and folders

        Utils.cleanFiles();
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
    }

    @AfterEach
    public void tearDownAfterClass() throws Exception {
        Utils.deleteFile("test_input.txt");
        Utils.deleteFile("index");
        Utils.deleteDirectory("objects");
        Utils.deleteFile(TEST_TREE_FILE);
        Utils.deleteFile("commit");
        Utils.deleteFile("head");

        // delete all the test files and folders

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

    @Test
    @DisplayName("Test if creating a Blob works")
    void testBlob() throws Exception {
        // Create a temporary test input file
        createTestInputFile(TEST_INPUT_FILE);

        try {
            Index index = new Index();
            index.add(TEST_INPUT_FILE);
            String generatedHash = Index.getBlobHash(TEST_INPUT_FILE);

            // Verify that the generated hash is not null
            assertNotNull(generatedHash);

            // Verify that the generated hash file exists in the "objects" directory
            File hashFile = new File(Paths.get(Paths.get("objects").toString(), generatedHash).toString());
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
        assertTrue(blobLines.contains("Blob : e58e1df02773bf212dee7a6082d2acc323ff4b02 : testFile1.txt"));
        assertTrue(blobLines.contains("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b"));

        // Remove an entry and generate the updated blob
        tree.remove("testFile1.txt");
        tree.generateBlob();
        File newTreeBlobFile = new File(Paths.get(Paths.get("objects").toString(), tree.getTreeSha()).toString());

        // Check if the removed entry is no longer present in the blob
        blobLines = Files.readAllLines(newTreeBlobFile.toPath());
        assertFalse(blobLines.contains("Blob : e58e1df02773bf212dee7a6082d2acc323ff4b02 : testFile1.txt"));

        // Clean up the tree blob file
        treeBlobFile.delete();
    }

    @Test
    @DisplayName("Test commit functionality #1")
    public void testCommit1() throws Exception {
        Commit c1 = new Commit("Paco", "initial commit");
        c1.addToTree("testFile1.txt");
        c1.addToTree("testFile2.txt");
        c1.save();

        // tests begin

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
    }

    @Test
    @DisplayName("Test commit functionality #2")
    public void testCommit2() throws Exception {

        File folder1 = new File("folder1");
        folder1.mkdir();
        File subfile = new File(folder1.getPath(), "subfile.txt");
        subfile.createNewFile();

        Commit c1 = new Commit("Paco", "initial commit");
        c1.addToTree("testFile1.txt");
        c1.addToTree("testFile2.txt");
        c1.save();

        Commit c2 = new Commit(c1.generateSha1(), "Paco", "second commit");
        c2.addToTree("testFile3.txt");
        c2.addToTree("testFile.txt");
        String directorySha = c2.tree.addDirectory(folder1.getName());
        c2.save();

        // tests begin

        Path commitFile1 = Paths.get("objects", c1.generateSha1());
        // the sha1 exists
        assertTrue(Files.exists(commitFile1));
        File f = new File(commitFile1.toString());
        BufferedReader br = new BufferedReader(new FileReader(f));
        br.readLine();
        // prev sha should be blank
        assertEquals("", br.readLine());
        // next sha should not be blank
        assertNotEquals("", br.readLine());
        br.close();

        Path commitFile2 = Paths.get("objects", c2.generateSha1());
        // the sha1 exists
        assertTrue(Files.exists(commitFile2));
        File f2 = new File(commitFile2.toString());
        BufferedReader br2 = new BufferedReader(new FileReader(f2));
        br2.readLine();
        // prev sha should not be blank
        assertNotEquals("", br2.readLine());
        // next sha should be blank
        assertEquals("", br2.readLine());
        br2.close();
        // the object created by the add directory method should have this sha
        assertEquals("e88bd5b6cf00b8369f37cd55ab53d551f0b7e9ec", directorySha);
    }

    @Test
    @DisplayName("Test commit functionality #3")
    public void testCommit3() throws Exception {

        Commit c1 = new Commit("Paco", "initial commit");
        c1.addToTree("testFile1.txt");
        c1.addToTree("testFile2.txt");
        c1.save();

        Commit c2 = new Commit(c1.generateSha1(), "Paco", "second commit");
        c2.addToTree("testFile3.txt");
        c2.addToTree("testFile4.txt");
        String directorySha = c2.tree.addDirectory("folder1");
        c2.save();

        Commit c3 = new Commit(c2.generateSha1(), "Paco", "third commit");
        c3.addToTree("testFile5.txt");
        c3.addToTree("testFile6.txt");
        String directorySha2 = c3.tree.addDirectory("folder2");
        c3.save();

        Commit c4 = new Commit(c3.generateSha1(), "Paco", "fourth commit");
        c4.addToTree("testFile7.txt");
        c4.addToTree("testFile8.txt");
        c4.save();

        // tests begin

        Path commitFile1 = Paths.get("objects", c1.generateSha1());
        // the sha1 exists
        assertTrue(Files.exists(commitFile1));
        File f = new File(commitFile1.toString());
        BufferedReader br = new BufferedReader(new FileReader(f));
        br.readLine();
        // prev sha should be blank
        assertEquals("", br.readLine());
        // next sha should not be blank
        assertNotEquals("", br.readLine());
        br.close();
        assertEquals("242a6cf30b877c3a51d0a35ce844d677a704885a", c2.getCommitTree(c2.generateSha1()));

        Path commitFile2 = Paths.get("objects", c2.generateSha1());
        // the sha1 exists
        assertTrue(Files.exists(commitFile2));
        File f2 = new File(commitFile2.toString());
        BufferedReader br2 = new BufferedReader(new FileReader(f2));
        br2.readLine();
        // prev sha should not be blank
        assertNotEquals("", br2.readLine());
        // next sha should not be blank
        assertNotEquals("", br2.readLine());
        br2.close();
        // the object created by the add directory method should have this sha
        assertEquals("e88bd5b6cf00b8369f37cd55ab53d551f0b7e9ec", directorySha);
        // assert the correct tree hash
        assertEquals("242a6cf30b877c3a51d0a35ce844d677a704885a", c2.getCommitTree(c2.generateSha1()));

        Path commitFile3 = Paths.get("objects", c3.generateSha1());
        // the sha1 exists
        assertTrue(Files.exists(commitFile3));
        File f3 = new File(commitFile3.toString());
        BufferedReader br3 = new BufferedReader(new FileReader(f3));
        br3.readLine();
        // prev sha should not be blank
        assertNotEquals("", br3.readLine());
        // next sha should not be blank
        assertNotEquals("", br3.readLine());
        br3.close();
        // the object created by the add directory method should have this sha
        assertEquals("b8885535247c9e8685f56fd310784eae77e697dc", directorySha2);
        // assert the correct tree hash
        assertEquals("4367ce72e38ed8910ec262b9666408cca88a0160", c3.getCommitTree(c3.generateSha1()));

        Path commitFile4 = Paths.get("objects", c4.generateSha1());
        // the sha1 exists
        assertTrue(Files.exists(commitFile4));
        File f4 = new File(commitFile4.toString());
        BufferedReader br4 = new BufferedReader(new FileReader(f4));
        br4.readLine();
        // prev sha should not be blank
        assertNotEquals("", br4.readLine());
        // next sha should be blank
        assertEquals("", br4.readLine());
        br4.close();
        // the object created by the add directory method should have this sha
        assertEquals("e88bd5b6cf00b8369f37cd55ab53d551f0b7e9ec", directorySha);
        // assert the correct tree hash
        assertEquals("d63320a5ecdeed39a0eb968c3a681ec4acad6126", c4.getCommitTree(c4.generateSha1()));

        // testing the tree contents of the final commit
        File commitTree = new File(
                Paths.get(Paths.get("objects").toString(), c4.getCommitTree(c4.generateSha1())).toString());
        BufferedReader treeReader = new BufferedReader(new FileReader(commitTree));
        ArrayList<String> treeContents = new ArrayList<String>();
        while (treeReader.ready()) {
            treeContents.add(treeReader.readLine());
        }
        treeReader.close();
        int counter = 0;
        String[] expectedContents = { "tree : 3e6c06b1a28a035e21aa0a736ef80afadc43122c",
                "Blob : 4953c2b1913260668adbe0067a6d87797d74a39e : testFile7.txt",
                "Blob : 82fa94ce3a531cbe5d17cb47089aa055f4e12b82 : testFile8.txt" };
        for (String treeLine : treeContents) {
            assertEquals(expectedContents[counter], treeLine);
            counter++;
        }
    }
}