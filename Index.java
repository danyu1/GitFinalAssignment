import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Index {
    private int totalBlobs;
    private static ArrayList<String> keyValuePairs;

    // initialize private instance variables
    // New file path to the objects folder
    // Create the 'objects' folder if it doesn't exist
    // Create index file
    public Index() throws IOException {
        this.totalBlobs = 0;
        keyValuePairs = new ArrayList<String>();
        String objectsFolderName = "objects";
        Path objectFolderPath = Paths.get(objectsFolderName);
        if (!Files.exists(objectFolderPath))
            Files.createDirectories(objectFolderPath);

        File file = new File("index");
        Path indexFolderPath = Paths.get("index");
        // Create the 'index' file if it doesn't exist
        if (!Files.exists(indexFolderPath))
            file.createNewFile();
    }

    public boolean containsBlob(String fileName) {
        for (String entry : keyValuePairs) {
            if (entry.contains(fileName))
                return true;
        }
        return false;
    }

    // creates a sha1 hash of the passed file and updates the index file to record
    // appropriate key:value pair
    public void add(String fileName) throws Exception {
        Path p = Paths.get("index");
        String SHA1 = Blob.generateSHA1(fileName);
        if (!keyValuePairs.contains(fileName + " : " + SHA1)) {
            StringBuilder sb = new StringBuilder("");
            Blob.createBlob(fileName);
            keyValuePairs.add(fileName + " : " + SHA1);
            totalBlobs++;
            for (int i = 0; i < keyValuePairs.size(); i++) {
                if (totalBlobs == 1) {
                    sb.append(format(keyValuePairs.get(i)));
                } else {
                    // makes sure that \n is printed appropriately
                    if (i == 0) {
                        sb.append(format(keyValuePairs.get(i)));
                    } else {
                        sb.append("\n" + format(keyValuePairs.get(i)));
                    }
                }
            }
            Files.writeString(p, sb.toString(), StandardCharsets.ISO_8859_1);
        }
    }

    public void addTree(String directoryName) throws Exception {
        File file = new File(directoryName);
        Tree t = new Tree();
        if (file.isDirectory()) {
            String hashedTree = t.addDirectory(directoryName);
            Path p = Paths.get("index");
            if (!keyValuePairs.contains("tree : " + hashedTree)) {
                StringBuilder sb = new StringBuilder();
                keyValuePairs.add("tree : " + hashedTree);
                totalBlobs++;
                for (int i = 0; i < keyValuePairs.size(); i++) {
                    if (totalBlobs == 1) {
                        sb.append(keyValuePairs.get(i) + " : " + directoryName);
                    } else {
                        if (i == 0) {
                            sb.append(keyValuePairs.get(i) + " : " + directoryName);
                        } else {
                            sb.append("\n" + keyValuePairs.get(i) + " : " + directoryName);
                        }
                    }
                }
                Files.writeString(p, sb.toString(), StandardCharsets.ISO_8859_1);
            }
        } else {
            throw new Exception("This is not a valid directory");
        }
    }

    public String format(String valuePair) {
        String formatted = "blob : ";
        formatted += valuePair.substring(valuePair.lastIndexOf(":") + 1);
        formatted += valuePair.substring(0, valuePair.lastIndexOf(":") - 1);
        return formatted;
    }

    // create a path to the index file
    // remove the keyValuePair from the arrayList
    // create a path to access the sha1 file
    // delete the sha1 hash file
    public void remove(String fileName) throws Exception {
        int i = 0;
        Path p = Paths.get("index");
        StringBuilder sb = new StringBuilder("");
        boolean removed = false;
        String currentLine = "";
        String SHA1 = Blob.generateSHA1(fileName);
        String keyValuePair = fileName + " : " + SHA1;
        keyValuePairs.remove(keyValuePair);
        // Path sha1Path = Paths.get("objects", SHA1);
        // Files.delete(sha1Path);
        BufferedReader br = new BufferedReader(new FileReader("index"));
        while (br.ready()) {
            currentLine = br.readLine();
            // if the currentline that is read not equal to the key value pair you want to
            // remove then append it to the stringbuilder
            if (!currentLine.equals(keyValuePair)) {
                if (totalBlobs == 1) {
                    sb.append(currentLine);
                } else {
                    if (i == 0) {
                        sb.append(currentLine);
                        i++;
                    } else {
                        sb.append("\n" + currentLine);
                        i++;
                    }
                }
            } else {
                removed = true;
                totalBlobs--;
            }
        }
        br.close();
        if (removed) {
            Files.writeString(p, sb.toString(), StandardCharsets.ISO_8859_1);
            System.out.println("Succesfully removed blob.");
        } else {
            System.out.println("Blob not found.");
        }
    }

    public static void main(String[] args) throws Exception {
        Index i = new Index();
        i.add("test.txt");
        i.add("test_input.txt");
        i.add("input.txt");
        // i.add("junit_example_test2.txt");
        i.remove("test.txt");
    }
}