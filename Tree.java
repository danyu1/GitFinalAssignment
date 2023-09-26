import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class Tree {
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\BlobandIndexRonanUpdated";

    private List<String> entries = new ArrayList<>();
    private String sha1;

    public void add(String entry) {
        entries.add(entry);
    }

    public void remove(String entry) {
        entries.remove(entry);
    }

    // "save" method
    public void generateBlob() throws IOException, NoSuchAlgorithmException {
        // Create a StringBuilder to concatenate all entries
        StringBuilder content = new StringBuilder();
        for (String entry : entries) {
            content.append(entry).append("\n");
        }

        // Calculate the SHA-1 hash of the content
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = digest.digest(content.toString().getBytes());
        sha1 = byteArrayToHex(hashBytes);

        // Create the blob file in the 'objects' folder
        Path blobPath = Paths.get(pathToWorkSpace + "\\objects", sha1);
        Files.write(blobPath, content.toString().getBytes());
    }

    public String getSha1() {
        return sha1;
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

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Tree tree = new Tree();

        // Add entries to the tree
        tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
        tree.add("blob : 01d82591292494afd1602d175e165f94992f6f5f : file2.txt");
        tree.add("blob : f1d82236ab908c86ed095023b1d2e6ddf78a6d83 : file3.txt");
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        tree.add("tree : e7d79898d3342fd15daf6ec36f4cb095b52fd976");

        // Generate and save the tree blob
        tree.generateBlob();

        System.out.println("Tree SHA1: " + tree.getSha1());
    }
}