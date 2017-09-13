package business;

/**
 * Created by Thales on 13/09/2017.
 */
public class KeyPair {

    private Key privateKey;
    private Key publicKey;

    public KeyPair(Key publicKey, Key privateKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public KeyPair(long publicKeyID, long privateKeyID, String keyName) {
        this.publicKey = new Key(publicKeyID, keyName);
        this.privateKey = new Key(privateKeyID, keyName);
    }

    public KeyPair(long publicKeyID, String publicKeyName) {
        this.publicKey = new Key(publicKeyID, publicKeyName);
        this.privateKey = null;
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Key privateKey) {
        this.privateKey = privateKey;
    }

    public Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }
}
