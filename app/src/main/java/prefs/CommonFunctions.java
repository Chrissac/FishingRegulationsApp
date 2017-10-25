package prefs;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
/**
 * Created by csacripante on 20/07/2017.
 */

public class CommonFunctions {
    public static String PassWordMd5(String s)
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")),0,s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static int validate(String email, String password) {

        String Email = email.toString();
        String Password = password.toString();

        if (Email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            return 1;
        }

        if (Password.isEmpty() || password.length() < 4 || Password.length() > 10) {
            return 2;
        }
        return 0;
    }
    public static int validate(String email) {

        String Email = email.toString();

        if (Email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            return 1;
        }
        return 0;
    }
    public static String generatePassword() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        uuid = uuid.substring(0,7);
        return uuid;
    }

}
