import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommitTest {
    private Commit firstCommit;
    private Commit secondCommit;
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\BlobandIndexRonanUpdated";

    @BeforeEach
    void setUp() throws Exception {
        firstCommit = new Commit("Paco H.",
                "This commit is missing the next commit and previous commit because it is the most recent one");
        secondCommit = new Commit(firstCommit.generateSha1(), "Paco H.", "This commit is aight.");
    }

    @AfterEach
    void tearDown() throws Exception {
        Path commitPath1 = Paths.get(pathToWorkSpace + "\\objects", firstCommit.generateSha1());
        Path commitPath2 = Paths.get(pathToWorkSpace + "\\objects", secondCommit.generateSha1());
        Files.delete(commitPath1);
        Files.delete(commitPath2);
    }

    @Test
    @DisplayName("[1] Test the createTree() method.")
    void testCreateTree() throws Exception {
        Assertions.assertNotNull(firstCommit.tree);
        Assertions.assertNotNull(secondCommit.tree);
    }

    @Test
    @DisplayName("[2] Test the generateSha1() method.")
    void testGenerateSha1() throws Exception {
        String sha1First = firstCommit.generateSha1();
        String sha1Second = secondCommit.generateSha1();

        // test that the sha string is not null
        Assertions.assertNotNull(sha1First);
        Assertions.assertNotNull(sha1First);
        // test if the sha is the proper length
        Assertions.assertEquals(40, sha1First.length());
        Assertions.assertEquals(40, sha1Second.length());

    }

    @Test
    @DisplayName("[3] Test the getDate() method.")
    void testGetDate() throws Exception {
        String date1 = firstCommit.getDate();
        String date2 = secondCommit.getDate();
        // test that the date is not null
        Assertions.assertNotNull(date1);
        Assertions.assertNotNull(date2);
    }

    @Test
    @DisplayName("[4] Test the save() method.")
    void testSave() throws Exception {
        Path commitPath1 = Paths.get(pathToWorkSpace + "\\objects", firstCommit.generateSha1());
        Path commitPath2 = Paths.get(pathToWorkSpace + "\\objects", secondCommit.generateSha1());
        // test that both the commits exist
        Assertions.assertTrue(Files.exists(commitPath1));
        Assertions.assertTrue(Files.exists(commitPath2));
    }
}
