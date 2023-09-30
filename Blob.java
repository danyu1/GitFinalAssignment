import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Blob {
    private Path p;
    private String fileName;

    // write given text to the file and path created
    public void writeToFile(String textToWrite) throws IOException {
        try {
            Files.writeString(p, textToWrite, StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // return the SHA1 of a file
    // MessageDigest supports different hash algorithms
    // Convert the byte array to a hexadecimal string
    public String generateSHA1(String fileName) throws Exception {
        File fileToHash = new File(Paths.get(fileName).toString());
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        BufferedReader br = new BufferedReader(new FileReader(fileToHash));
        StringBuilder contents = new StringBuilder();
        while (br.ready()) {
            contents.append(br.readLine());
        }
        br.close();
        byte[] hash = md.digest(contents.toString().getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // convert the original file into byte
    // New file path that accesses objects folder which can access SHA1 file
    // Create a new file with the SHA-1 hash as the filename inside 'objects' folder
    public void createBlob(String fileName) throws Exception {
        File originalFile = new File(Paths.get(fileName).toString());
        StringBuilder contents = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        while (br.ready()) {
            contents.append(br.readLine());
        }
        br.close();
        String objectsFolderPath = "objects";
        Path objectFilePath = Paths.get(objectsFolderPath, generateSHA1(fileName));
        Files.write(objectFilePath, contents.toString().getBytes());
        System.out.println("New file created with SHA-1 hash as filename: " + objectFilePath);
    }

    public Path getPath() {
        return this.p;
    }

    public static void main(String[] args) throws Exception {
        Blob blob = new Blob();
        blob.createBlob("test.txt");
    }
}