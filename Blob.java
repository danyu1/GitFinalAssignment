import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class Blob {

    // return the SHA1 of a file
    // MessageDigest supports different hash algorithms
    // Convert the byte array to a hexadecimal string
    public static String generateSHA1(String fileName) throws Exception {
        File fileToHash = new File(Paths.get(fileName).toString());
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        StringBuilder contents = new StringBuilder();
        if (!fileName.equals("")) {
            BufferedReader br = new BufferedReader(new FileReader(fileToHash));
            while (br.ready()) {
                contents.append(br.readLine());
            }
            br.close();
        } else {
            contents.append("");
        }
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

    public static String generateSHA1WithPath(String pathToBlob) throws Exception {
        File fileToHash = new File(pathToBlob);
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

    public static String readFile(String fileName) throws Exception {
        File file = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder contents = new StringBuilder();
        while (br.ready()) {
            contents.append(br.readLine());
        }
        br.close();
        return contents.toString();
    }

    // convert the original file into byte
    // New file path that accesses objects folder which can access SHA1 file
    // Create a new file with the SHA-1 hash as the filename inside 'objects' folder
    public static void createBlob(String fileName) throws Exception {
        File originalFile = new File(Paths.get(fileName).toString());
        StringBuilder contents = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        while (br.ready()) {
            contents.append(br.readLine());
        }
        br.close();
        Path objectFilePath = Paths.get(Paths.get("objects").toString() + "/" + generateSHA1(fileName));
        Files.write(objectFilePath, contents.toString().getBytes());
        System.out.println("New file created with SHA-1 hash as filename: " + objectFilePath);
    }

    public static void createBlobWithPath(String pathToBlob) throws Exception {
        File originalFile = new File(pathToBlob);
        StringBuilder contents = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        while (br.ready()) {
            contents.append(br.readLine());
        }
        br.close();
        Path objectFilePath = Paths.get(Paths.get("objects").toString() + "/" + generateSHA1WithPath(pathToBlob));
        File blob = new File(objectFilePath.toString());
        blob.createNewFile();
        Files.write(objectFilePath, contents.toString().getBytes());
        System.out.println("New file created with SHA-1 hash as filename: " + objectFilePath);
    }

    public static void main(String[] args) throws Exception {
        Blob.createBlob("test.txt");
    }
}