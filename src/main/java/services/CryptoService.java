package services;

import business.Message;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import utils.Base64Helper;
import utils.Utils;
import utils.VAEHelper;
import utils.Vpkcs11Session;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static sun.security.pkcs11.wrapper.PKCS11Constants.*;

/**
 * Created by Thales on 13/09/2017.
 */
public class CryptoService {

    public static final byte[] iv = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F};

    public static final CK_MECHANISM encMechCbcPad = new CK_MECHANISM (CKM_AES_CBC_PAD, iv);
    public static final CK_MECHANISM encMechCtr    = new CK_MECHANISM (CKM_AES_CTR    , iv);
    public static final CK_MECHANISM encMechCbc    = new CK_MECHANISM (CKM_AES_CBC, iv);

    public static final CK_MECHANISM sha256Mech      = new CK_MECHANISM (CKM_SHA256);
    public static final CK_MECHANISM sha384Mech      = new CK_MECHANISM (CKM_SHA384);
    public static final CK_MECHANISM sha512Mech      = new CK_MECHANISM (CKM_SHA512);
    public static final CK_MECHANISM hmacSha256Mech  = new CK_MECHANISM (CKM_SHA256_HMAC);

    public Message encryptMessage(String pinCode, String keyName, String messageToEncrypt) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);
        long keyID = VAEHelper.findKey(session, keyName);
        if (keyID == 0) {
            throw new IllegalArgumentException("Key does not exist");
        }
        byte[] plainBytes = messageToEncrypt.getBytes();
        try {
            byte[] encryptedBytes = VAEHelper.encryptBuf(session, encMechCbcPad, keyID, plainBytes);
            return new Message(Base64Helper.byteArrayToString(encryptedBytes));
        } catch (Exception e) {
            throw new RuntimeException("Encryption process does not work");
        }
    }

    public Message decryptMessage(String pinCode, String keyName, String encryptedMessage) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);
        long keyID = VAEHelper.findKey(session, keyName);
        if (keyID == 0) {
            throw new IllegalArgumentException("Key does not exist");
        }
        byte[] encryptedBytes = Base64Helper.stringToByteArray(encryptedMessage);
        try {
            byte[] plainBytes = VAEHelper.decryptBuf(session, encMechCbcPad, keyID, encryptedBytes);
            return new Message(new String(plainBytes));
        } catch (Exception e) {
            throw new RuntimeException("Encryption process does not work");
        }
    }

    public Message digestMessage(String pinCode, String keyName, String messageToDigest) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);

        long keyID = VAEHelper.findKey(session, keyName);
        if (keyID == 0) {
            throw new IllegalArgumentException("Key does not exist");
        }
        //64 is used according to digest function (sha512 produces hashes of 64 chars)
        try {
            byte[] hashBytes = VAEHelper.digest(session, sha512Mech, 64, messageToDigest.getBytes(), 0);
            return new Message(Utils.toHex(hashBytes));
        } catch (Exception e) {
            throw new RuntimeException("Digest process does not work");
        }
    }

    public Message signMessage(String pinCode, String keyName, String messageToSign) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);

        //2048 is used because of RSA2048
        CK_MECHANISM signMech = new CK_MECHANISM (CKM_RSA_PKCS);

        long keyID = VAEHelper.findKey(session, keyName, CKO_PRIVATE_KEY);
        if (keyID == 0) {
            throw new IllegalArgumentException("Private key does not exist");
        }
        try {
            byte[] signature = VAEHelper.sign(session, signMech, messageToSign, keyID);
            return new Message(Base64Helper.byteArrayToString(signature));
        } catch (Exception e) {
            throw new RuntimeException("Signing process does not work");
        }
    }

    public Message verifySignature(String pinCode, String keyName, String message, String signature) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);

        //2048 is used because of RSA2048
        CK_MECHANISM signMech = new CK_MECHANISM (CKM_RSA_PKCS);

        long keyID = VAEHelper.findKey(session, keyName, CKO_PUBLIC_KEY);
        if (keyID == 0) {
            throw new IllegalArgumentException("Public key does not exist");
        }
        try {
            return new Message(Boolean.toString(VAEHelper.verifySignature(session, signMech, message, Base64Helper.stringToByteArray(signature), keyID)));
        } catch (Exception e) {
            throw new RuntimeException("Verifying signature does not work");
        }
    }
}
