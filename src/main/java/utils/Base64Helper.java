package utils;

import java.util.Base64;

/**
 * Created by Thales on 14/09/2017.
 */
public class Base64Helper {

    public static String byteArrayToString(byte[] byteArray) {
        return Base64.getEncoder().encodeToString(byteArray);
    }

    public static byte[] stringToByteArray(String string) {
        return Base64.getDecoder().decode(string);
    }
}
