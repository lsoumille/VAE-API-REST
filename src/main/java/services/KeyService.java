package services;

import business.Key;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import utils.VAEHelper;
import utils.Vpkcs11Session;

import java.util.HashMap;
import java.util.Map;

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

    public void deleteKey(String pinCode, String name) {
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
    }
}
