package services;

import business.Key;
import business.KeyPair;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import utils.VAEHelper;
import utils.Vpkcs11Session;

import java.util.HashMap;
import java.util.Map;

import static sun.security.pkcs11.wrapper.PKCS11Constants.CKM_RSA_PKCS_KEY_PAIR_GEN;
import static sun.security.pkcs11.wrapper.PKCS11Constants.CKO_PRIVATE_KEY;
import static sun.security.pkcs11.wrapper.PKCS11Constants.CKO_PUBLIC_KEY;

/**
 * Created by Thales on 13/09/2017.
 */
public class KeyService {

    public Key createKey(String pinCode, String name) {
        Key key = null;
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);
        Long keyID = VAEHelper.findKey(session, name);
        if (keyID == 0)
        {
            System.out.println ("The key not found, creating it..." );
            keyID = VAEHelper.createKey(session, name);

            if(keyID != 0) {
                System.out.println ("Key successfully Generated. Key Handle: " + keyID);
                key = new Key(keyID, name);
            } else {
                throw new RuntimeException("Key was not generated");
            }
        }
        else
        {
            throw new IllegalArgumentException("Key already exists with " + name + " name");
        }

        VAEHelper.closeDown(session);
        return key;
    }

    public Key getKey(String pinCode, String name) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);
        long keyID = VAEHelper.findKey(session, name);
        if (keyID == 0)
        {
            throw new IllegalArgumentException("Key does not exist");
        }
        VAEHelper.closeDown(session);
        return new Key(keyID, name);
    }

    public Key deleteKey(String pinCode, String name) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);
        long keyID = VAEHelper.findKey(session, name);
        if (keyID == 0)
        {
            throw new IllegalArgumentException("Key does not exist");
        } else {
            try {
                VAEHelper.deleteKey(session, keyID);
            } catch (PKCS11Exception e) {
                throw new RuntimeException("Key was not deleted");
            }
        }
        VAEHelper.closeDown(session);
        return new Key(keyID, name);
    }

    public KeyPair createKeyPair(String pinCode, String keyName) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);
        KeyPair keyPair = null;
        long publicKeyID = VAEHelper.findKey(session, keyName, CKO_PUBLIC_KEY);
        long privateKeyID = VAEHelper.findKey(session, keyName, CKO_PRIVATE_KEY);
        if (publicKeyID == 0 && privateKeyID == 0) {
                long[] keyIDs = VAEHelper.createKeyPair(session, keyName, new CK_MECHANISM(CKM_RSA_PKCS_KEY_PAIR_GEN), 2048);
                //The public key is always the first one
                publicKeyID = keyIDs[0];
                privateKeyID = keyIDs[1];
                keyPair = new KeyPair(publicKeyID, privateKeyID, keyName);
        }
        else {
            throw new IllegalArgumentException("Key pair already exists");
        }
        VAEHelper.closeDown(session);
        return keyPair;
    }

    public KeyPair getKeyPair(String pinCode, String keyName) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);
        long publicKeyID = VAEHelper.findKey(session, keyName, CKO_PUBLIC_KEY);
        if (publicKeyID == 0) {
            throw new IllegalArgumentException("Public Key does not exist");
        }
        long privateKeyID = VAEHelper.findKey(session, keyName, CKO_PRIVATE_KEY);
        if (privateKeyID == 0) {
            throw new IllegalArgumentException("Private key does not exist");
        }
        VAEHelper.closeDown(session);
        return new KeyPair(publicKeyID, privateKeyID, keyName);
    }

    public KeyPair deleteKeyPair(String pinCode, String keyName) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);
        long keyID = VAEHelper.findKey(session, keyName);
        if (keyID == 0)
        {
            throw new IllegalArgumentException("Key does not exist");
        } else {
            try {
                VAEHelper.deleteKey(session, keyID);
            } catch (PKCS11Exception e) {
                throw new RuntimeException("Key was not deleted");
            }
        }
        VAEHelper.closeDown(session);
        return new KeyPair(keyID, keyName);
    }
}
