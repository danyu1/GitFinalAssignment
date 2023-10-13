import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommitTest {
    static Commit firstCommit;
    static Commit secondCommit;

    @BeforeAll
    static void setUp() throws Exception {
        Utils.cleanFiles();
        firstCommit = new Commit("Paco H.",
                "initial commit");
        secondCommit = new Commit(firstCommit.generateSha1(), "Paco H.", "second commit");
        firstCommit.save();
        secondCommit.save();
    }

    @AfterAll
    static void tearDown() throws Exception {
        Path commitPath1 = Paths.get("objects", firstCommit.generateSha1());
        Path commitPath2 = Paths.get("objects", secondCommit.generateSha1());
        Files.delete(commitPath1);
        Files.delete(commitPath2);
    }

    @Test
    @DisplayName("[1] Test the createTree() method.")
    void testCreateTree() throws Exception {
        assertNotNull(firstCommit.tree);
        assertNotNull(secondCommit.tree);
    }

    @Test
    @DisplayName("[2] Test the generateSha1() method.")
    void testGenerateSha1() throws Exception {
        String sha1First = firstCommit.generateSha1();
        String sha1Second = secondCommit.generateSha1();

        // test that the sha string is not null
        assertNotNull(sha1First);
        assertNotNull(sha1First);
        // test if the sha is the proper length
        assertEquals(40, sha1First.length());
        assertEquals(40, sha1Second.length());

    }

    @Test
    @DisplayName("[3] Test the getDate() method.")
    void testGetDate() throws Exception {
        String date1 = firstCommit.getDate();
        String date2 = secondCommit.getDate();
        // test that the date is not null
        assertNotNull(date1);
        assertNotNull(date2);
    }

    @Test
    @DisplayName("[4] Test the save() method.")
    void testSave() throws Exception {
        Path commitPath1 = Paths.get("objects", firstCommit.generateSha1());
        Path commitPath2 = Paths.get("objects", secondCommit.generateSha1());
        // test that both the commits exist
        assertTrue(Files.exists(commitPath1));
        assertTrue(Files.exists(commitPath2));
    }
}
