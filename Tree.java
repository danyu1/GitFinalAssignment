import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class Tree {
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment";

    private List<String> entries = new ArrayList<>();
    private String sha1;
    private String directorySha1;

    public void add(String entry) throws Exception {
        entries.add(entry);
        updateTreeFile();
    }

    public void remove(String entry) throws Exception {
        for (String currentEntry : entries) {
            currentEntry.contains(entry);
            entries.remove(currentEntry);
        }
        updateTreeFile();
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
                    childTree.add("tree : " + shaOfDirectoryBlobs + " : " + currentFile.getName());
                }
                // currentFile is not a directory
                else {
                    childTree.add("Blob : " + Blob.generateSHA1WithPath(currentFile.getPath()) + " : "
                            + currentFile.getName());
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
    public void updateTreeFile() throws Exception {
        StringBuilder sb = new StringBuilder("");
        BufferedReader br = new BufferedReader(
                new FileReader(new File(Paths.get(pathToWorkSpace + "\\tree").toString())));
        while (br.ready()) {
            sb.append(br.readLine() + "\n");
        }
        br.close();
        for (String entry : entries) {
            sb.append(entry).append("\n");
        }
        Files.write(Paths.get(pathToWorkSpace + "\\tree"), sb.toString().getBytes());
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

        // tree.add("blob : f5cda28ce12d468c64a6a2f2224971f894442f1b :
        // junit_example_test1.txt");
        // tree.add("blob : 50d4b41eed4faffe212d8cf6ec89d7889dfeff9e :
        // junit_example_test2.txt");
        // tree.remove("blob : f5cda28ce12d468c64a6a2f2224971f894442f1b :
        // junit_example_test1.txt");
        // tree.generateBlob();
        // Add entries to the tree
        // tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
        // tree.add("blob : 01d82591292494afd1602d175e165f94992f6f5f : file2.txt");
        // tree.add("blob : f1d82236ab908c86ed095023b1d2e6ddf78a6d83 : file3.txt");
        // tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        // tree.add("tree : e7d79898d3342fd15daf6ec36f4cb095b52fd976");

        // // Generate and save the tree blob
        // tree.generateBlob();

        // System.out.println("Tree SHA1: " + tree.getSha1());

        tree.addDirectory("C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\testDirectory1");
        File parentDirectoryFile = new File(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\objects\\fb360f9c09ac8c5edb2f18be5de4e80ea4c430d0");
        System.out.println(parentDirectoryFile.exists());
        System.out.println(parentDirectoryFile.toString());
        // tree.addDirectory("C:\\Users\\danie\\OneDrive\\Desktop\\Topics
        // Repos\\GitFinalAssignment\\testDirectory2");

    }
}