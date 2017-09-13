package utils;

/**
 * Created by Thales on 13/09/2017.
 */
public class Utils {
    public static String toHex(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            result += String.format("%02x", bytes[i]);
        }
        return result;
    }
}
