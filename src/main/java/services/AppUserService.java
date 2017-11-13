package services;

import business.AppUser;
import business.Message;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import utils.Base64Helper;
import utils.VAEHelper;
import utils.Vpkcs11Session;

import java.io.IOException;

import static services.CryptoService.encMechCbcPad;

public class AppUserService {

    public AppUser encryptUser(String pinCode, String keyName, String firstname, String lastname, String address, String city) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);

        long keyID = VAEHelper.findKey(session, keyName);
        if (keyID == 0) {
            throw new IllegalArgumentException("Key does not exist");
        }

        byte[] firstnameBytes = firstname.getBytes();
        byte[] lastnameBytes = lastname.getBytes();

        try {
            byte[] encryptedFirstname = VAEHelper.encryptBuf(session, encMechCbcPad, keyID, firstnameBytes);
            byte[] encryptedLastname = VAEHelper.encryptBuf(session, encMechCbcPad, keyID, lastnameBytes);
            firstname = Base64Helper.byteArrayToString(encryptedFirstname);
            lastname = Base64Helper.byteArrayToString(encryptedLastname);
        } catch (Exception e) {
            throw new RuntimeException("Encryption process does not work");
        }

        if (address != null) {
            byte[] addressBytes = address.getBytes();
            try {
                byte[] encryptedAddress = VAEHelper.encryptBuf(session, encMechCbcPad, keyID, addressBytes);
                address = Base64Helper.byteArrayToString(encryptedAddress);
            } catch (Exception e) {
                throw new RuntimeException("Encryption process does not work");
            }
        }
        if (city != null) {
            byte[] cityBytes = city.getBytes();
            try {
                byte[] encryptedCity = VAEHelper.encryptBuf(session, encMechCbcPad, keyID, cityBytes);
                city = Base64Helper.byteArrayToString(encryptedCity);
            } catch (Exception e) {
                throw new RuntimeException("Encryption process does not work");
            }
        }

        return new AppUser(firstname,
                lastname,
                address,
                city);
    }

    public AppUser decryptUser(String pinCode, String keyName, String encryptedFirstname, String encryptedLastname, String encryptedAddress, String encryptedCity) {
        Vpkcs11Session session = VAEHelper.startUp(VAEHelper.getPKCS11LibPath(null), pinCode);

        long keyID = VAEHelper.findKey(session, keyName);
        if (keyID == 0) {
            throw new IllegalArgumentException("Key does not exist");
        }

        byte[] encryptedFirstnameBytes = Base64Helper.stringToByteArray(encryptedFirstname);
        byte[] encryptedLastnameBytes = Base64Helper.stringToByteArray(encryptedLastname);

        byte[] firstnameBytes = new byte[]{};
        byte[] lastnameBytes = new byte[]{};
        try {
            firstnameBytes = VAEHelper.decryptBuf(session, encMechCbcPad, keyID, encryptedFirstnameBytes);
            lastnameBytes = VAEHelper.decryptBuf(session, encMechCbcPad, keyID, encryptedLastnameBytes);
        } catch (Exception e) {
            throw new RuntimeException("Decryption process does not work");
        }

        byte[] addressBytes = new byte[]{};
        if (encryptedAddress != null) {
            byte[] encryptedAddressBytes = Base64Helper.stringToByteArray(encryptedAddress);
            try {
                addressBytes = VAEHelper.decryptBuf(session, encMechCbcPad, keyID, encryptedAddressBytes);
            } catch (PKCS11Exception e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] cityBytes = new byte[]{};
        if (encryptedCity != null) {
            byte[] encryptedCityBytes = Base64Helper.stringToByteArray(encryptedCity);
            try {
                cityBytes = VAEHelper.decryptBuf(session, encMechCbcPad, keyID, encryptedCityBytes);
            } catch (PKCS11Exception e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new AppUser(new String(firstnameBytes), new String(lastnameBytes), new String(addressBytes), new String(cityBytes));
    }
}
