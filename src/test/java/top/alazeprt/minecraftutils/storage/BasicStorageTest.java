package top.alazeprt.minecraftutils.storage;

import top.alazeprt.minecraftutils.account.OfflineAccount;

public class BasicStorageTest {

    public static BasicStorage basicStorage = new BasicStorage();

    static {
        basicStorage.create();
        basicStorage.saveAccount(new OfflineAccount("test"));
    }
}
