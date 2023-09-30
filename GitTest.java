// import static org.junit.Assert.*;
// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.security.NoSuchAlgorithmException;
// import java.util.List;

// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;

// public class GitTest {

// private static final String TEST_INPUT_FILE = "test_input.txt";
// private static final String TEST_INDEX_FOLDER = "test_index";
// private static final String TEST_OBJECTS_FOLDER = "test_objects";
// private static final String TEST_TREE_FILE = "test_tree.txt";

// @BeforeAll
// static void setUpBeforeClass() throws Exception {

// Utils.writeStringToFile("junit_example_file_data.txt", "test file contents");
// Utils.deleteFile("Index.txt");
// Utils.deleteDirectory("Objects");
// Utils.deleteFile("Tree");
// Utils.deleteFile(TEST_TREE_FILE);

// }

// @AfterAll
// static void tearDownAfterClass() throws Exception {
// // Utils.deleteFile("junit_example_file_data.txt");
// // Utils.deleteFile("Index.txt");
// // Utils.deleteDirectory("Objects");
// // Utils.deleteFile("Tree");
// Utils.deleteFile("test_input.txt");
// Utils.deleteFile("Index.txt");
// Utils.deleteDirectory("Objects");
// Utils.deleteFile(TEST_TREE_FILE);
// }

// @Test
// @DisplayName("Test if creating a Blob works")
// void testBlob() throws IOException, NoSuchAlgorithmException {
// // Create a temporary test input file
// createTestInputFile(TEST_INPUT_FILE);

// try {
// String generatedHash = Blob.createBlob(TEST_INPUT_FILE);

// // Verify that the generated hash is not null
// assertNotNull(generatedHash);

// // Verify that the generated hash file exists in the "objects" directory
// File hashFile = new File("./objects/" + generatedHash);
// assertTrue(hashFile.exists());

// // Clean up the generated hash file
// hashFile.delete();
// } finally {
// // Clean up the test input file
// deleteTestInputFile(TEST_INPUT_FILE);
// }
// }

// // Utility method to create a test input file
// private void createTestInputFile(String filename) throws IOException {
// FileWriter writer = new FileWriter(filename);
// writer.write("This is a test.");
// writer.close();
// }

// // Utility method to delete a test input file
// private void deleteTestInputFile(String filename) {
// File file = new File(filename);
// if (file.exists()) {
// file.delete();
// }
// }

// @Test
// @DisplayName("Test if Initializing works - index and objects folders need to
// be created")
// void testInit() throws Exception {
// // Ensure that the test index and objects folders don't exist before calling
// // init
// deleteTestFolder(TEST_INDEX_FOLDER);
// deleteTestFolder(TEST_OBJECTS_FOLDER);

// // Call the init method
// Index.init();

// // Check if the index and objects folders were created
// assertTrue(folderExists(TEST_INDEX_FOLDER));
// assertTrue(folderExists(TEST_OBJECTS_FOLDER));

// // Clean up by deleting the test folders
// deleteTestFolder(TEST_INDEX_FOLDER);
// deleteTestFolder(TEST_OBJECTS_FOLDER);
// }

// // Utility method to check if a folder exists
// private boolean folderExists(String folderName) {
// File folder = new File(folderName);
// return folder.exists() && folder.isDirectory();
// }

// // Utility method to delete a test folder
// private void deleteTestFolder(String folderName) {
// File folder = new File(folderName);
// if (folder.exists() && folder.isDirectory()) {
// File[] files = folder.listFiles();
// if (files != null) {
// for (File file : files) {
// file.delete();
// }
// }
// folder.delete();
// }
// }

// @Test
// @DisplayName("Test if adding a Blob works - index gets updated and object
// folder adds the blob")
// void testAdd() throws Exception {
// Index index = new Index();

// // Create a temporary test input file
// createTestInputFile(TEST_INPUT_FILE);

// try {
// // Add a blob to the index
// index.addBlob(TEST_INPUT_FILE);

// // Check if the index has been updated with the blob
// assertTrue(index.containsBlob(TEST_INPUT_FILE));

// // Check if the blob file exists in the "objects" directory
// File blobFile = new File("./objects/" + index.getBlobHash(TEST_INPUT_FILE));
// assertTrue(blobFile.exists());
// } finally {
// // Clean up the test input file and the added blob
// deleteTestInputFile(TEST_INPUT_FILE);
// index.remove(TEST_INPUT_FILE);
// }
// }

// @Test
// @DisplayName("Test if removing a Blob works - index gets updated and object
// folder removes the blob")
// void testRemove() throws Exception {
// Index index = new Index();

// // Create a temporary test input file and add it to the index
// createTestInputFile(TEST_INPUT_FILE);
// index.addBlob(TEST_INPUT_FILE);

// // Check if the blob is initially in the index
// assertTrue(index.containsBlob(TEST_INPUT_FILE));

// try {
// // Remove the blob from the index
// index.removeBlob(TEST_INPUT_FILE);

// // Check if the index has been updated and no longer contains the blob
// assertFalse(index.containsBlob(TEST_INPUT_FILE));

// // Check if the blob file has been removed from the "objects" directory
// File blobFile = new File("./objects/" + index.getBlobHash(TEST_INPUT_FILE));
// assertFalse(blobFile.exists());
// } finally {
// // Clean up the test input file
// deleteTestInputFile(TEST_INPUT_FILE);
// }
// }

// @Test
// @DisplayName("Test Tree Class: Adding, Removing Entries, and Generating
// Blob")
// void testTreeClass() throws IOException, NoSuchAlgorithmException {
// Tree tree = new Tree();

// // Add entries to the tree
// tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
// tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");

// // Generate and save the tree blob
// tree.generateBlob();

// // Check if the tree blob file exists in the 'objects' folder
// File treeBlobFile = new File("./objects/" + tree.getSha1());
// assertTrue(treeBlobFile.exists());

// // Read the contents of the tree blob file
// List<String> blobLines = Files.readAllLines(treeBlobFile.toPath());

// // Check if the blob contains the expected entries
// assertTrue(blobLines.contains("blob :
// 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt"));
// assertTrue(blobLines.contains("tree :
// bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b"));

// // Remove an entry and generate the updated blob
// tree.remove("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
// tree.generateBlob();

// // Check if the removed entry is no longer present in the blob
// blobLines = Files.readAllLines(treeBlobFile.toPath());
// assertTrue(blobLines.contains("blob :
// 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt"));

// // Clean up the tree blob file
// treeBlobFile.delete();
// }
// }