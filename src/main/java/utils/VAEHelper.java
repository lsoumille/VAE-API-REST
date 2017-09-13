package utils;

import sun.security.pkcs11.wrapper.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;

import static sun.security.pkcs11.wrapper.PKCS11Constants.*;

/**
 * Created by Thales on 13/09/2017.
 */
public class VAEHelper {
    public static final String UnixInstallPath = "/opt/vormetric/DataSecurityExpert/agent/pkcs11/lib/libvorpkcs11.so";
    public static final String WinX86InstallPath = "C:\\Program Files (x86)\\Vormetric\\DataSecurityExpert\\Agent\\pkcs11\\bin\\vorpkcs11.dll";
    public static final String WinX64InstallPath = "C:\\Program Files\\Vormetric\\DataSecurityExpert\\Agent\\pkcs11\\bin\\vorpkcs11.dll";

    private static Map<Long, String> PKCS11ConstantNames;

    public static void saveKey(byte[] keyValue, String keyFileName)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(keyFileName);
            out.write(keyValue);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {

        }
    }
    public static boolean isFileExist(String filePath)
    {
        File f = new File(filePath);
        return f.exists();
    }

    public static long findKey(Vpkcs11Session session, String keyName)
    {
        /* Find the key on the DSM */
        CK_ATTRIBUTE[] attrs = new CK_ATTRIBUTE[]
                {
                        new CK_ATTRIBUTE (CKA_LABEL, keyName),
                };

        try
        {
            /* Call C_FindObjectsFinal in case there is another find objects going on  */
            session.p11.C_FindObjectsFinal (session.sessionHandle);
            session.p11.C_FindObjectsInit (session.sessionHandle, attrs);
            long[] keyID = session.p11.C_FindObjects (session.sessionHandle, 1);
            session.p11.C_FindObjectsFinal (session.sessionHandle);
            if (keyID.length > 0)
            {
                return keyID[0];
            }
            else
            {
                /* System.out.println ("The key: " + keyName + " is not found."); */
                return 0;
            }
        }
        catch (PKCS11Exception e)
        {
            System.out.println (e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.println ("Exception thrown.");
            System.out.println (e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }


    public static long findKey(Vpkcs11Session session, String keyName, long keyClass)
    {
        /* Find the key on the DSM */
        CK_ATTRIBUTE[] attrs = new CK_ATTRIBUTE[]
                {
                        new CK_ATTRIBUTE (CKA_LABEL, keyName),
                        new CK_ATTRIBUTE(CKA_CLASS, keyClass)
                };

        try
        {
            /* Call C_FindObjectsFinal in case there is another find objects going on  */
            session.p11.C_FindObjectsFinal (session.sessionHandle);
            session.p11.C_FindObjectsInit (session.sessionHandle, attrs);
            long[] keyID = session.p11.C_FindObjects (session.sessionHandle, 1);
            session.p11.C_FindObjectsFinal (session.sessionHandle);
            if (keyID.length > 0)
            {
                return keyID[0];
            }
            else
            {
                /* System.out.println ("The key: " + keyName + " is not found."); */
                return 0;
            }
        }
        catch (PKCS11Exception e)
        {
            System.out.println (e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.println ("Exception thrown.");
            System.out.println (e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public static String getPKCS11LibPath(String libPath)
    {
        String path = null;
        if (( libPath != null ) && (!libPath.equals("")))
        {
            path = libPath;
        }
        else
        {
            path = System.getenv("VPKCS11LIBPATH");

            if ((path == null) || (path.equals("")))
            {
                String osName = System.getProperty("os.name");
                if (osName.contains("Windows"))
                {
                    if (isFileExist(WinX64InstallPath))
                    {
                        path = WinX64InstallPath;
                    }else if ( isFileExist(WinX86InstallPath))
                    {
                        path = WinX86InstallPath;
                    }
                }
                else
                {
                    if (isFileExist(UnixInstallPath))
                    {
                        path = UnixInstallPath;
                    }
                }

                if ((path == null )|| (path.equals("")))
                {
                    System.out.println ("Cannot find Vormetric PKCS11 library, please install Vormetric key agent. " );
                    System.exit(4);
                }
            }
            else
            {
                if ( isFileExist(path))
                {
                    // this is okay
                }
                else
                {
                    System.out.println ("VPKCS11LIBPATH point to a file that does not exist: " + path);
                    System.exit(4);
                }
            }
        }
        System.out.println ("Loading the Vormetric PKCS11 library from : " + path);
        return  path;
    }

    // compare two binary files return true if the content is equal false otherwise
    public static boolean CompareTwoFiles(String pathA, String pathB)
    {
        boolean match = false;
        try
        {
            int BLOCK_SIZE = 4096;
            int bytesReadA, bytesReadB;
            // assume inputStreamA and inputStreamB are streams from your two files.
            byte[] streamABlock = new byte[BLOCK_SIZE];
            byte[] streamBBlock = new byte[BLOCK_SIZE];
            FileInputStream inputStreamA = new FileInputStream(pathA);
            FileInputStream inputStreamB = new FileInputStream(pathB);

            do
            {
                bytesReadA = inputStreamA.read(streamABlock);
                bytesReadB = inputStreamB.read(streamBBlock);
                match = ((bytesReadA == bytesReadB) && Arrays.equals( streamABlock, streamBBlock));
            } while (match && (bytesReadA > -1));
        }
        catch (Exception e)
        {
            System.out.println ("Exception thrown.");
            System.out.println (e.getMessage());
        }

        return match;
    }

    public static long[] createKeyPair(Vpkcs11Session session, String keyName, CK_MECHANISM mechanism, int modulusBits)
    {
        long[] keyIDArr = null;
        byte[] publicExponent = { 0x01, 0x00, 0x01, 0x00 };
        String year, month, day;
        Date date = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, 30);

        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH)+1); // Calendar is zero based!
        day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

        System.out.println("Current End Date: year: "+ year+ " month: "+ month+ " day: "+day);
        CK_DATE endDate = new CK_DATE(year.toCharArray(), month.toCharArray(), day.toCharArray());

        CK_ATTRIBUTE[] publicKeyAttr = new CK_ATTRIBUTE[]
                {
                        new CK_ATTRIBUTE (CKA_LABEL, keyName),
                        new CK_ATTRIBUTE (CKA_CLASS, CKO_PUBLIC_KEY),
                        new CK_ATTRIBUTE (CKA_ENCRYPT, true),
                        new CK_ATTRIBUTE (CKA_SIGN, true),
                        new CK_ATTRIBUTE (CKA_VERIFY, true),
                        new CK_ATTRIBUTE (CKA_WRAP, true),
                        new CK_ATTRIBUTE (CKA_TOKEN, true),
                        new CK_ATTRIBUTE (CKA_PUBLIC_EXPONENT, publicExponent),
                        new CK_ATTRIBUTE (CKA_MODULUS_BITS, modulusBits),
                        new CK_ATTRIBUTE (CKA_END_DATE, endDate)
                };

        CK_ATTRIBUTE[] privateKeyAttr = new CK_ATTRIBUTE[]
                {
                        new CK_ATTRIBUTE (CKA_CLASS, CKO_PRIVATE_KEY),
                        new CK_ATTRIBUTE (CKA_TOKEN, true),
                        new CK_ATTRIBUTE (CKA_PRIVATE, true),
                        new CK_ATTRIBUTE (CKA_SENSITIVE, true),
                        new CK_ATTRIBUTE (CKA_DECRYPT, true),
                        new CK_ATTRIBUTE (CKA_SIGN, true),
                        new CK_ATTRIBUTE (CKA_UNWRAP, true),
                        new CK_ATTRIBUTE (CKA_END_DATE, endDate)
                };
        try {
            keyIDArr = session.p11.C_GenerateKeyPair(session.sessionHandle, mechanism, publicKeyAttr, privateKeyAttr);
        }
        catch(PKCS11Exception pkex) {
            pkex.printStackTrace();
        }
        return keyIDArr;
    }

    public static void deleteKey(Vpkcs11Session session, long keyId) throws PKCS11Exception {
        session.p11.C_DestroyObject (session.sessionHandle, keyId);
        System.out.println ("Successfully deleted the key from DSM.");
    }

    public static long createKey(Vpkcs11Session session, String keyName)
    {
        long keyID = 0;
        String year, month, day;
        /* Create an AES 256 key on the DSM without pass in key value */
        try {
            Date date = new Date();
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.DATE, 30);

            year = String.valueOf(cal.get(Calendar.YEAR));
            month = String.valueOf(cal.get(Calendar.MONTH)+1); // Calendar is zero based!
            day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

            System.out.println("Current End Date: year: "+ year+ " month: "+ month+ " day: "+day);
            CK_DATE endDate = new CK_DATE(year.toCharArray(), month.toCharArray(), day.toCharArray());
            CK_MECHANISM mechanism = new CK_MECHANISM (CKM_AES_KEY_GEN);

            CK_ATTRIBUTE[] attrs = new CK_ATTRIBUTE[]
                    {
                            new CK_ATTRIBUTE (CKA_LABEL, keyName),
                            new CK_ATTRIBUTE (CKA_CLASS, CKO_SECRET_KEY),
                            new CK_ATTRIBUTE (CKA_KEY_TYPE, CKK_AES),
                            new CK_ATTRIBUTE (CKA_VALUE_LEN, 32),
                            new CK_ATTRIBUTE (CKA_TOKEN, true),
                            new CK_ATTRIBUTE (CKA_ENCRYPT, true),
                            new CK_ATTRIBUTE (CKA_DECRYPT, true),
                            new CK_ATTRIBUTE (CKA_SIGN, false),
                            new CK_ATTRIBUTE (CKA_VERIFY, false),
                            new CK_ATTRIBUTE (CKA_WRAP, true),
                            new CK_ATTRIBUTE (CKA_UNWRAP, true),
                            new CK_ATTRIBUTE (CKA_EXTRACTABLE, false),
                            new CK_ATTRIBUTE (CKA_ALWAYS_SENSITIVE, false),
                            new CK_ATTRIBUTE (CKA_NEVER_EXTRACTABLE, true),
                            new CK_ATTRIBUTE (CKA_SENSITIVE, true),
                            new CK_ATTRIBUTE (0x80000061, true),
                            new CK_ATTRIBUTE (0x80000063, 44640),
                            new CK_ATTRIBUTE (CKA_END_DATE, endDate),
                    };

            System.out.println ("Before generating Key. Key Handle: " + keyID);
            keyID = session.p11.C_GenerateKey (session.sessionHandle, mechanism, attrs);
            System.out.println ("Key successfully Generated. Key Handle: " + keyID);
        }
        catch (PKCS11Exception e)
        {
            e.printStackTrace();
        }
        return keyID;
    }

    public static long createKeyObject(Vpkcs11Session session, String keyName, String appName, String keyValue) {
        long keyID = 0;
        try {
            /* Create an AES 256 key on the DSM */

            /* AES key template.
	         * CKA_LABEL is the name of the key and will be displayed on the DSM
	         * CKA_VALUE is the bytes that make up the AES key.
	        */

            CK_ATTRIBUTE[] attrs = new CK_ATTRIBUTE[]
                    {
                            new CK_ATTRIBUTE(CKA_LABEL, keyName),
                            new CK_ATTRIBUTE(CKA_APPLICATION, appName),
                            new CK_ATTRIBUTE(CKA_CLASS, CKO_SECRET_KEY),
                            new CK_ATTRIBUTE(CKA_KEY_TYPE, CKK_AES),
                            new CK_ATTRIBUTE(CKA_VALUE, keyValue),
                            new CK_ATTRIBUTE(CKA_VALUE_LEN, 32),
                            new CK_ATTRIBUTE(CKA_TOKEN, true),
                            new CK_ATTRIBUTE(CKA_ENCRYPT, true),
                            new CK_ATTRIBUTE(CKA_DECRYPT, true),
                            new CK_ATTRIBUTE(CKA_SIGN, false),
                            new CK_ATTRIBUTE(CKA_VERIFY, false),
                            new CK_ATTRIBUTE(CKA_WRAP, true),
                            new CK_ATTRIBUTE(CKA_UNWRAP, true),
                            new CK_ATTRIBUTE(CKA_EXTRACTABLE, false),
                            new CK_ATTRIBUTE(CKA_ALWAYS_SENSITIVE, false),
                            new CK_ATTRIBUTE(CKA_NEVER_EXTRACTABLE, true),
                            new CK_ATTRIBUTE(CKA_SENSITIVE, true)
                    };

            keyID = session.p11.C_CreateObject(session.sessionHandle, attrs);
            System.out.println("Object successfully created. Object Handle: " + keyID);
        } catch (PKCS11Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return keyID;
    }

    public static Vpkcs11Session startUp(String libPath, String pin)
    {
        try
        {
            Vpkcs11Session session = new Vpkcs11Session();

            /* Initialization of the PKCS11 instance, open session and login */
            session.p11 = PKCS11.getInstance(libPath, "C_GetFunctionList", null, false);
            long[] slots = session.p11.C_GetSlotList (false);
            loadConstantNames();

            session.sessionHandle = session.p11.C_OpenSession (slots[0], 0,  null, null);
            System.out.println ("Session successfully opened. Handle: " + session.sessionHandle);
            session.p11.C_Login (session.sessionHandle, CKU_USER, pin.toCharArray());
            System.out.println ("Successfully Logged in");
            return session;
        }
        catch (PKCS11Exception e)
        {
            System.out.println (e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.println ("Exception thrown.");
            System.out.println (e.getMessage());
        }
        return null;
    }

    public static void closeDown(Vpkcs11Session session)
    {
        /* Logout and close session */
        try
        {
            session.p11.C_Logout(session.sessionHandle);
            System.out.println ("Successfully logged out.");
            session.p11.C_CloseSession (session.sessionHandle);
            System.out.println ("Successfully closed session.");
        }
        catch (PKCS11Exception e)
        {
            System.out.println (e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.println ("Exception thrown.");
            System.out.println (e.getMessage());
        }
    }

    public static String getConstantName(long constVal) {
        if (PKCS11ConstantNames == null) {
            loadConstantNames();
        }
        return PKCS11ConstantNames.get(constVal);
    }

    public static void loadConstantNames() {
        Map<Long, String> cNames = new HashMap<Long, String>();
        String fieldName;

        for (Field field : PKCS11Constants.class.getDeclaredFields()) {
            // ((field.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) != 0)
            fieldName = field.getName();
            if (long.class == field.getType() && fieldName.startsWith("CKA")) {
                try {
                    // only record final static int fields
                    cNames.put(Long.parseLong(field.get(null).toString()), fieldName);
                    // System.out.println("Putting: "+fieldName+" with "+field.get(null));
                } catch(IllegalAccessException iae)
                {
                    System.out.println(iae.getMessage());
                }
            }
        }
        PKCS11ConstantNames = cNames;
    }

    public static byte[] encryptBuf(Vpkcs11Session session, CK_MECHANISM encMech, long keyID, byte[] plainBytes) throws PKCS11Exception, IOException {
        byte[] encryptedText;
        int encryptedDataLen = 0;
        byte[] outText = {};

        int plainBytesLen = plainBytes.length;
        session.p11.C_EncryptInit(session.sessionHandle, encMech, keyID);
        System.out.println("C_EncryptInit success.");

        encryptedDataLen = session.p11.C_Encrypt(session.sessionHandle, plainBytes, 0, plainBytesLen, outText, 0, 0);
        System.out.println("C_Encrypt success. Encrypted data len = " + encryptedDataLen);

        encryptedText = new byte[encryptedDataLen];
        encryptedDataLen = session.p11.C_Encrypt(session.sessionHandle, plainBytes, 0, plainBytesLen, encryptedText, 0, encryptedDataLen);
        System.out.println("C_Encrypt 2nd call succeed. Encrypted data len = " + encryptedDataLen);
        return encryptedText;
    }


    public static byte[] decryptBuf(Vpkcs11Session session, CK_MECHANISM encMech, long keyID, byte[] encryptedBytes) throws PKCS11Exception, IOException {
        byte[] decryptedBytes;
        byte[] decryptedData;
        int encryptedDataLen = encryptedBytes.length;
        int decryptedDataLen = 0;
        byte[] outText = {};
        System.out.println("Start Decryption");
        session.p11.C_DecryptInit(session.sessionHandle, encMech, keyID);
        System.out.println("C_DecryptInit success.");

        decryptedDataLen = session.p11.C_Decrypt(session.sessionHandle, encryptedBytes, 0, encryptedDataLen, outText, 0, 0);
        System.out.println("C_Decrypt success. Decrypted data length = " + decryptedDataLen);

        decryptedData = new byte[decryptedDataLen];
        decryptedDataLen = session.p11.C_Decrypt(session.sessionHandle, encryptedBytes, 0, encryptedDataLen, decryptedData, 0, decryptedDataLen);
        System.out.println("C_Decrypt 2nd call succeed. Decrypted data length = " + decryptedDataLen);

        decryptedBytes = new byte[decryptedDataLen];
        System.arraycopy(decryptedData, 0, decryptedBytes, 0, decryptedDataLen);
        return decryptedBytes;
    }

    public static byte[] digest(Vpkcs11Session session, CK_MECHANISM mech, int digestSize, byte[] input, long key) throws Exception {
        byte[] result = null;
        session.p11.C_DigestInit(session.sessionHandle, mech);
        if (key != 0) {
            session.p11.C_DigestKey(session.sessionHandle, key);
        }
        session.p11.C_DigestUpdate(session.sessionHandle, 0, input, 0, input.length);
        result = new byte[digestSize];
        int size = session.p11.C_DigestFinal(session.sessionHandle, result, 0, digestSize);
        return result;
    }
    /*
    public static long[] createKeyPair(Vpkcs11Session session, String publicKeyName, String privateKeyName) throws PKCS11Exception {
        CK_MECHANISM mechanism = new CK_MECHANISM (CKM_RSA_PKCS_KEY_PAIR_GEN);
        byte[] publicExponent = { 0x01, 0x00, 0x01, 0x00 };
        int modulusBits = 2048;

        CK_ATTRIBUTE[] publicKeyAttr = new CK_ATTRIBUTE[]
                {
                        new CK_ATTRIBUTE (CKA_LABEL, publicKeyName),
                        new CK_ATTRIBUTE (CKA_CLASS, CKO_PUBLIC_KEY),
                        new CK_ATTRIBUTE (CKA_ENCRYPT, true),
                        new CK_ATTRIBUTE (CKA_SIGN, true),
                        new CK_ATTRIBUTE (CKA_VERIFY, true),
                        new CK_ATTRIBUTE (CKA_WRAP, true),
                        new CK_ATTRIBUTE (CKA_TOKEN, true),
                        new CK_ATTRIBUTE (CKA_PUBLIC_EXPONENT, publicExponent),
                        new CK_ATTRIBUTE (CKA_MODULUS_BITS, modulusBits)
                };

        CK_ATTRIBUTE[] privateKeyAttr = new CK_ATTRIBUTE[]
                {
                        new CK_ATTRIBUTE (CKA_LABEL, privateKeyName),
                        new CK_ATTRIBUTE (CKA_CLASS, CKO_PRIVATE_KEY),
                        new CK_ATTRIBUTE (CKA_TOKEN, true),
                        new CK_ATTRIBUTE (CKA_PRIVATE, true),
                        new CK_ATTRIBUTE (CKA_SENSITIVE, true),
                        new CK_ATTRIBUTE (CKA_DECRYPT, true),
                        new CK_ATTRIBUTE (CKA_SIGN, true),
                        new CK_ATTRIBUTE (CKA_UNWRAP, true)
                };
        return session.p11.C_GenerateKeyPair (session.sessionHandle, mechanism, publicKeyAttr, privateKeyAttr);
    }*/
}
