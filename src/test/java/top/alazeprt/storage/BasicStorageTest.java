package top.alazeprt.storage;

import top.alazeprt.account.OfflineAccount;

public class BasicStorageTest {

    public static BasicStorage basicStorage = new BasicStorage();

    static {
        basicStorage.create();
        basicStorage.saveAccount(new OfflineAccount("test"));
    }
}
