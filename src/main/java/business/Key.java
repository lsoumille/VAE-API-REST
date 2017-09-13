package business;

/**
 * Created by Thales on 13/09/2017.
 */
public class Key {

    private long keyId;
    private String keyName;

    public Key(String keyName) {
        this.keyName = keyName;
    }

    public Key(long keyId, String keyName) {
        this.keyId = keyId;
        this.keyName = keyName;
    }

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
