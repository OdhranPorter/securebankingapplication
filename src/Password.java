import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Password {
    // length of random salt in bytes
    private static final int SALT_LENGTH = 16;
    // number of hash iterations
    private static final int ITERATIONS = 65536;
    // desired key length in bits
    private static final int KEY_LENGTH = 256;

    public static byte[] generateSalt() {
        // create secure random instance
        SecureRandom random = new SecureRandom();
        // generate salt bytes
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    public static byte[] hashPassword(char[] password, byte[] salt) {
        try {
            // prepare key specification for pbkdf2
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            // get secret key factory instance
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            // generate hashed key and return encoded bytes
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // wrap exceptions in runtime exception
            throw new RuntimeException("error hashing password " + e.getMessage(), e);
        }
    }

    public static String toHex(byte[] array) {
        // convert byte array to hex string
        StringBuilder sb = new StringBuilder();
        for (byte b : array) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static byte[] fromHex(String hex) {
        // convert hex string back to byte array
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                + Character.digit(hex.charAt(i+1), 16));
        }
        return bytes;
    }
}
