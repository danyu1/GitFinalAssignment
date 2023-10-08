import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class Tree {
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment";

    private List<String> entries = new ArrayList<>();
    private String sha1;
    private String directorySha1;

    public Tree() throws Exception {
        File file = new File("tree");
        if (!file.exists())
            file.createNewFile();
    }

    // add method should accept a fileName, or a tree string
    // tree : HASH : folderName
    public void add(String fileName) throws Exception {
        File fileToAdd = new File(fileName);
        if (!fileToAdd.exists()) {
            String isTree = fileName.substring(0, 6);
            if (!isTree.equals("tree :")) {
                throw new Exception("Invalid file to add");
            }
        }
        if (fileToAdd.exists()) {
            entries.add(fileName);
            updateTreeFileAdd();
        } else {

        }
    }

    public void remove(String entry) throws Exception {
        for (String currentEntry : entries) {
            currentEntry.contains(entry);
            entries.remove(currentEntry);
        }
        updateTreeFileAdd();
    }

    public static String addDirectory(String directoryPath) throws Exception {
        Tree childTree = new Tree();
        Path pathToFolder = Paths.get(directoryPath);
        File currentDirectory = new File(pathToFolder.toString());
        File[] files = currentDirectory.listFiles();
        if (files.length == 0) {
            childTree.add("");
            return childTree.getTreeSha();
        }
        if (Files.isDirectory(pathToFolder)) {
            for (File currentFile : files) {
                if (currentFile.isDirectory()) {
                    String subDirectoryPath = directoryPath + "\\" + currentFile.getName();
                    String shaOfDirectoryBlobs = addDirectory(subDirectoryPath);
                    String toAdd = "tree : " + shaOfDirectoryBlobs + " : " + currentFile.getName();
                    childTree.add(toAdd);
                }
                // currentFile is not a directory
                else {
                    String toAdd = "Blob : " + Blob.generateSHA1WithPath(currentFile.getPath()) + " : "
                            + currentFile.getName();
                    childTree.add(toAdd);
                    Blob.createBlobWithPath(currentFile.getPath());
                }
            }
        } else {
            throw new Exception("Invalid path or folder does not exist.");
        }
        childTree.generateBlob();
        return childTree.getTreeSha();
    }

    // in this case "tree" file is called index due to previous choices made in
    // other classes
    public void updateTreeFileAdd() throws Exception {
        StringBuilder currentTreeFile = new StringBuilder("");
        BufferedReader br = new BufferedReader(
                new FileReader(new File(Paths.get(pathToWorkSpace + "\\tree").toString())));
        while (br.ready()) {
            currentTreeFile.append(br.readLine() + "\n");
        }
        br.close();
        for (String entry : entries) {
            if (!entry.contains("tree : ") && !entry.contains("Blob : ")) {
                if (!currentTreeFile.toString().contains(entry))
                    currentTreeFile.append("Blob : " + Blob.generateSHA1(entry)).append(" : " + entry).append("\n");
            } else {
                if (!currentTreeFile.toString().contains(entry)) {
                    currentTreeFile.append(entry).append("\n");
                }
            }
        }
        br.close();
        Files.write(Paths.get(pathToWorkSpace + "\\tree"), currentTreeFile.toString().getBytes());
        generateTreeSHA();
    }

    public String getTreeSha() {
        return this.sha1;
    }

    public String generateTreeSHA() throws Exception {
        StringBuilder toHash = new StringBuilder("");
        for (String entry : entries) {
            int lastColonIndex = entry.lastIndexOf(":");
            toHash.append(entry.substring(lastColonIndex + 1).trim());
        }

        // Calculate the SHA-1 hash of the content
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = digest.digest(toHash.toString().getBytes());
        this.sha1 = byteArrayToHex(hashBytes);
        return this.sha1;
    }

    // "save" method
    public void generateBlob() throws Exception {
        // Create a StringBuilder to concatenate all entries
        StringBuilder content = new StringBuilder("");
        for (String entry : entries) {
            content.append(entry).append("\n");
        }
        // Create the blob file in the 'objects' folder
        Path blobPath = Paths.get(pathToWorkSpace + "\\objects\\", generateTreeSHA());
        Files.write(blobPath, content.toString().getBytes());
    }

    // Utility method to convert byte array to hexadecimal string
    private static String byteArrayToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public List<String> getEntries() {
        return this.entries;
    }

    public String returnAllEntries() {
        StringBuilder sb = new StringBuilder("");
        for (String objects : entries) {
            sb.append(objects);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        Tree tree = new Tree();
        // Add entries to the tree
        tree.add("testFile1.txt");
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");

        // Generate and save the tree blob
        tree.generateBlob();

        // Check if the tree blob file exists in the 'objects' folder
        File treeBlobFile = new File(Paths.get(Paths.get("objects").toString(), tree.getTreeSha()).toString());
        System.out.println(treeBlobFile.exists());

        System.out.println("Tree SHA1: " + tree.getTreeSha());

        tree.addDirectory("C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\testDirectory1");
        File parentDirectoryFile = new File(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\objects\\fb360f9c09ac8c5edb2f18be5de4e80ea4c430d0");
        System.out.println(parentDirectoryFile.exists());
        System.out.println(parentDirectoryFile.toString());
        tree.addDirectory("C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\testDirectory2");

    }
}