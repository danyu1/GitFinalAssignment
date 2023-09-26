import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Blob {
    public static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\BlobandIndexRonanUpdated";

    public Blob(String string) {
    }

    public static String blob(String inputFile) throws IOException, NoSuchAlgorithmException {
        File file = new File(pathToWorkSpace + "\\" + inputFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            sb.append(line).append("");
        }
        reader.close();
        String hashed = generateSHA(sb.toString());
        write(hashed, sb);

        return hashed;
    }

    public static void write(String hashed, StringBuilder inside) throws IOException {
        String newFile = hashed;
        FileWriter write = new FileWriter(pathToWorkSpace + "\\objects\\" + newFile);
        write.write(inside.toString());
        write.close();
    }

    public static String generateSHA(String input) throws NoSuchAlgorithmException {
        try {
            // getInstance() method is called with algorithm SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        String file = "input.txt";
        blob(file);
    }
}