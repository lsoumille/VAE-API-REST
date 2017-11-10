package utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Created by Thales on 14/09/2017.
 */
public class Base64Helper {

    public static String byteArrayToString(byte[] byteArray) {
        //return Base64.getEncoder().encodeToString(byteArray);
        try {
            return new String(Base64.getEncoder().encode(byteArray), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] stringToByteArray(String string) {
        return Base64.getDecoder().decode(string);
    }
}
