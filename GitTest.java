import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GitTest {

    private static final String TEST_INPUT_FILE = "test_input.txt";
    private static final String TEST_TREE_FILE = "tree";

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        Utils.writeStringToFile("junit_example_file_data.txt", "test file contents");
        Utils.createFile("commit");
        Utils.createFile(TEST_TREE_FILE);
        Utils.createFile("head");
        // create all the test files and folders

        Utils.cleanFiles();
        Utils.createAllTestFile();
    }

    @AfterEach
    public void tearDownAfterClass() throws Exception {
        Utils.deleteDirectory("objects");
        Utils.deleteFile(TEST_TREE_FILE);
        Utils.deleteFile("commit");
        Utils.deleteFile("head");

        // delete all the test files and folders
        Utils.deleteAllTestFile();
        Utils.cleanFiles();
    }

    @Test
    @DisplayName("Test if creating a Blob works")
    void testBlob() throws Exception {
        // Create a temporary test input file
        createTestInputFile(TEST_INPUT_FILE);
        try {
            Tree tree = new Tree();
            tree.add(TEST_INPUT_FILE);
            String generatedHash = Blob.generateSHA1(TEST_INPUT_FILE);

            // Verify that the generated hash is not null
            assertNotNull(generatedHash);

            // Verify that the generated hash file exists in the "objects" directory
            File hashFile = new File(Paths.get(Paths.get("objects").toString(), generatedHash).toString());
            assertTrue(hashFile.exists());

            // Clean up the generated hash file
            hashFile.delete();
        } finally {
            // Clean up the test input file
            Utils.deleteFile(TEST_INPUT_FILE);
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

        Commit c1 = new Commit("Paco", "initial commit");
        c1.addToTree("testFile1.txt");
        c1.addToTree("testFile2.txt");
        c1.save();

        Commit c2 = new Commit(c1.generateSha1(), "Paco", "second commit");
        c2.addToTree("testFile3.txt");
        c2.addToTree("testFile4.txt");
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
        assertEquals("2cb662477a2a14dc76a2b8facbccc8e23f80b00f", directorySha);
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
        assertEquals("0d71e51848a485355702be02eedcb17454a5a1b5", c1.getCommitTree(c1.generateSha1()));

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
        assertEquals("2cb662477a2a14dc76a2b8facbccc8e23f80b00f", directorySha);
        // assert the correct tree hash
        assertEquals("2cb662477a2a14dc76a2b8facbccc8e23f80b00f", c2.getCommitTree(c2.generateSha1()));

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
        assertEquals("fe376074d15c48fffd95e80a9479acd164fb2438", directorySha2);
        // assert the correct tree hash
        assertEquals("fe376074d15c48fffd95e80a9479acd164fb2438", c3.getCommitTree(c3.generateSha1()));

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
        // assert the correct tree hash
        assertEquals("47d2ad7df6fe1511be79cf8d4ea170272bbfe8ea", c4.getCommitTree(c4.generateSha1()));

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
        String[] expectedContents = { "Blob : e58e1df02773bf212dee7a6082d2acc323ff4b02 : testFile1.txt",
                "Blob : d70b62c0f22581d040ff8e39ca34375c9a45647c : testFile2.txt",
                "Blob : fb74d67d3b5ed98eb6cbe66380a337a724a7778c : testFile3.txt",
                "Blob : 9f7d255ff5413910591c7dd8cb8cdf3fbb465c24 : testFile4.txt",
                "Blob : da39a3ee5e6b4b0d3255bfef95601890afd80709 : subfile.txt",
                "Blob : 2693be5876d9e17c4d16189f9e29555f1b99e622 : testFile5.txt",
                "Blob : 517f59bbce3538183e101bd0a4f9d1fda498a95b : testFile6.txt",
                "Blob : da39a3ee5e6b4b0d3255bfef95601890afd80709 : subfile2.txt",
                "Blob : 4953c2b1913260668adbe0067a6d87797d74a39e : testFile7.txt",
                "Blob : 82fa94ce3a531cbe5d17cb47089aa055f4e12b82 : testFile8.txt" };
        for (String treeLine : treeContents) {
            assertEquals(expectedContents[counter], treeLine);
            counter++;
        }
    }

    @Test
    @DisplayName("Test add and edit functionality")
    public void testAddAndEdit() throws Exception {
        Utils.cleanFiles();
        Files.write(Paths.get("testFile3.txt"), "test commit content 3".getBytes());
        Files.write(Paths.get("testFile5.txt"), "test commit content 5".getBytes());
        Commit c1 = new Commit("Paco", "initial commit");
        c1.addToTree("testFile1.txt");
        c1.addToTree("testFile2.txt");
        c1.save();

        Commit c2 = new Commit(c1.generateSha1(), "Paco", "second commit");
        c2.addToTree("testFile3.txt");
        c2.addToTree("testFile4.txt");
        c2.save();
        Files.write(Paths.get("testFile3.txt"), "new edited content for file 3".getBytes());
        c2.tree.deleteOrEdit("*edited*testFile3.txt");
        c2.save();

        Commit c3 = new Commit(c2.generateSha1(), "Paco", "third commit");
        c3.addToTree("testFile5.txt");
        c3.addToTree("testFile6.txt");
        c3.save();
        c3.tree.deleteOrEdit("*deleted*testFile4.txt");
        c3.save();

        Files.write(Paths.get("testFile5.txt"), "new edited content for file 5".getBytes());
        Commit c4 = new Commit(c3.generateSha1(), "Paco", "fourth commit");
        c4.addToTree("testFile7.txt");
        c4.addToTree("testFile8.txt");
        c4.save();
        c4.tree.deleteOrEdit("*edited*testFile5.txt");
        c4.save();
        // test for the expected contents in the current tree file of the current commit
        // testfile4 should be gone, testfile 5 and 3 should be edited
        String[] expectedContents = { "Blob : e58e1df02773bf212dee7a6082d2acc323ff4b02 : testFile1.txt",
                "Blob : d70b62c0f22581d040ff8e39ca34375c9a45647c : testFile2.txt",

                "Blob : 9b86abefd1d050e7b004ce1e2bec9cd0005be1fa : testFile3.txt",
                "Blob : 517f59bbce3538183e101bd0a4f9d1fda498a95b : testFile6.txt",
                "Blob : 4953c2b1913260668adbe0067a6d87797d74a39e : testFile7.txt",
                "Blob : 82fa94ce3a531cbe5d17cb47089aa055f4e12b82 : testFile8.txt",
                "Blob : c74e1cc818fb612e7d544f60295f98f7b854f359 : testFile5.txt" };
        File treeFile = new File("tree");
        BufferedReader br = new BufferedReader(new FileReader(treeFile));
        int counter = 0;
        while (br.ready()) {
            assertEquals(expectedContents[counter], br.readLine());
            counter++;
        }
        br.close();
    }
}