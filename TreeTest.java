import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TreeTest {
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment";
    static Index index;
    static Tree tree;

    @BeforeAll
    static void setUpBefore() throws Exception {
        tree = new Tree();
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
    void testAdd() throws Exception {
        String blob1ToAdd = "blob : f5cda28ce12d468c64a6a2f2224971f894442f1b : junit_example_test1.txt";
        String blob2ToAdd = "blob : 50d4b41eed4faffe212d8cf6ec89d7889dfeff9e : junit_example_test2.txt";

        tree.add(blob1ToAdd);
        tree.add(blob2ToAdd);
        List<String> entries = tree.getEntries();
        Assertions.assertTrue(entries.contains(blob1ToAdd));
        Assertions.assertTrue(entries.contains(blob2ToAdd));
        tree.remove(blob1ToAdd);
        tree.remove(blob2ToAdd);
    }

    @Test
    void testGenerateBlob() throws Exception {
        String blob1ToAdd = "blob : f5cda28ce12d468c64a6a2f2224971f894442f1b : junit_example_test1.txt";
        String blob2ToAdd = "blob : 50d4b41eed4faffe212d8cf6ec89d7889dfeff9e : junit_example_test2.txt";

        tree.add(blob1ToAdd);
        tree.add(blob2ToAdd);
        tree.generateBlob();

        File f1 = new File(pathToWorkSpace + "\\objects", tree.getSha1());
        BufferedReader br = new BufferedReader(new FileReader(f1));

        // assert that the correct string is added to the tree file which should also
        // have the correct hash as its name
        assertEquals("blob : f5cda28ce12d468c64a6a2f2224971f894442f1b : junit_example_test1.txt",
                br.readLine());
        assertEquals("blob : 50d4b41eed4faffe212d8cf6ec89d7889dfeff9e : junit_example_test2.txt",
                br.readLine());
        br.close();

        // delete the tree file within the objects folder
        Path p1 = Paths.get(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\BlobandIndexRonanUpdated\\objects\\3caac0f41752bcce36e05d509612cff335688ccf");
        Files.delete(p1);
        tree.remove(blob1ToAdd);
        tree.remove(blob2ToAdd);
    }

    @Test
    void testGetSha1() throws Exception {
        String blob1ToAdd = "blob : f5cda28ce12d468c64a6a2f2224971f894442f1b : junit_example_test1.txt";
        String blob2ToAdd = "blob : 50d4b41eed4faffe212d8cf6ec89d7889dfeff9e : junit_example_test2.txt";

        tree.add(blob1ToAdd);
        tree.add(blob2ToAdd);
        tree.generateBlob();

        // assert that the getSha1 method returns the proper hash for the tree file
        assertEquals("3caac0f41752bcce36e05d509612cff335688ccf", tree.getSha1());

        // delete the tree file within the objects folder
        Path p1 = Paths.get(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\BlobandIndexRonanUpdated\\objects\\3caac0f41752bcce36e05d509612cff335688ccf");
        Files.delete(p1);
        tree.remove(blob1ToAdd);
        tree.remove(blob2ToAdd);
    }

    @Test
    void testRemove() throws Exception {
        String blob1ToAdd = "blob : f5cda28ce12d468c64a6a2f2224971f894442f1b : junit_example_test1.txt";
        String blob2ToAdd = "blob : 50d4b41eed4faffe212d8cf6ec89d7889dfeff9e : junit_example_test2.txt";

        tree.add(blob1ToAdd);
        tree.add(blob2ToAdd);
        tree.remove(blob1ToAdd);
        tree.generateBlob();

        File f1 = new File(pathToWorkSpace + "\\objects", tree.getSha1());
        BufferedReader br = new BufferedReader(new FileReader(f1));

        // assert that the tree file has removed the correct blob if the first blob was
        // removed than the first line of the file should contain the second file
        // created
        assertEquals("blob : 50d4b41eed4faffe212d8cf6ec89d7889dfeff9e : junit_example_test2.txt",
                br.readLine());
        br.close();

        // delete the tree file within the objects folder
        Path p1 = Paths.get(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\BlobandIndexRonanUpdated\\objects\\04ee905523810ffee3e5fe7f73e2825844674bbb");
        Files.delete(p1);
        tree.remove(blob2ToAdd);
    }

    @Test
    @DisplayName("Test add directory without nested folders.")
    void testAddDirectory1() throws Exception {
        File folder1 = new File("C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\test1");
        folder1.mkdir();
        File file1 = new File(folder1.getPath() + "\\examplefile1.txt");
        File file2 = new File(folder1.getPath() + "\\examplefile2.txt");
        File file3 = new File(folder1.getPath() + "\\examplefile3.txt");
        PrintWriter pw1 = new PrintWriter(file1);
        PrintWriter pw2 = new PrintWriter(file2);
        PrintWriter pw3 = new PrintWriter(file3);
        pw1.print("new contents for file one");
        pw2.print("new contents for file two");
        pw3.print("new contents for file three");
        pw1.close();
        pw2.close();
        pw3.close();

        Tree tree = new Tree();
        tree.addDirectory(folder1.getPath());

        assertEquals("43b30f483e15a64a6afe4096f805128407574940", tree.getDirectorySha());

        Files.delete(Paths.get(file1.getPath()));
        Files.delete(Paths.get(file2.getPath()));
        Files.delete(Paths.get(file3.getPath()));
        Files.delete(Paths.get(folder1.getPath()));
        Files.deleteIfExists(Paths.get(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\objects\\43b30f483e15a64a6afe4096f805128407574940"));

    }

    @Test
    @DisplayName("Test add directory with nested folders and files.")
    void testAddDirectory2() throws Exception {
        File folderRoot = new File(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\AdvancedTest");
        File folder1 = new File(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\AdvancedTest\\test3");
        File folder2 = new File(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\AdvancedTest\\test5");
        folderRoot.mkdir();
        folder1.mkdir();
        folder2.mkdir();
        File file1 = new File(folderRoot.getPath() + "\\examplefile1.txt");
        File file2 = new File(folderRoot.getPath() + "\\examplefile2.txt");
        File file3 = new File(folderRoot.getPath() + "\\examplefile3.txt");
        File subFile = new File(folder2.getPath() + "\\advancedTest.txt");
        PrintWriter pw1 = new PrintWriter(file1);
        PrintWriter pw2 = new PrintWriter(file2);
        PrintWriter pw3 = new PrintWriter(file3);
        PrintWriter subWriter = new PrintWriter(subFile);
        pw1.print("new contents for file one");
        pw2.print("new contents for file two");
        pw3.print("new contents for file three");
        subWriter.print("new contents for the sub file");
        pw1.close();
        pw2.close();
        pw3.close();
        subWriter.close();

        Tree tree = new Tree();
        tree.addDirectory(folderRoot.getPath());

        assertEquals("ae186a310d6f6bbb1d3ea9c2f0b456fec1d49adf", tree.getDirectorySha());

        Files.delete(Paths.get(subFile.getPath()));
        Files.delete(Paths.get(folder2.getPath()));
        Files.delete(Paths.get(folder1.getPath()));
        Files.delete(Paths.get(file1.getPath()));
        Files.delete(Paths.get(file2.getPath()));
        Files.delete(Paths.get(file3.getPath()));
        Files.delete(Paths.get(folderRoot.getPath()));
        Files.deleteIfExists(Paths.get(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\objects\\502495e9806be809cf5e009432fcc9714489a9bf"));
        Files.deleteIfExists(Paths.get(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\objects\\ae186a310d6f6bbb1d3ea9c2f0b456fec1d49adf"));
        Files.deleteIfExists(Paths.get(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\objects\\da39a3ee5e6b4b0d3255bfef95601890afd80709"));

    }
}
