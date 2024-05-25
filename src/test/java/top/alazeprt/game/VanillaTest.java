package top.alazeprt.game;

import top.alazeprt.account.Account;
import top.alazeprt.java.Java;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;

import static top.alazeprt.storage.BasicStorageTest.basicStorage;
import static top.alazeprt.version.InstanceTest.instance;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VanillaTest {

    private static String nativesFolder;

    @Test
    @Order(1)
    public void extractNativesTest() {
        nativesFolder = (String) Vanilla.extractNatives(instance).getData();
    }

    @Test
    @Order(2)
    public void launchTest() {
        Vanilla.launch(new Java("C:\\Program Files\\Zulu\\zulu-21\\bin\\java.exe", 21),
                instance, (Account) basicStorage.getAccount("test").getData(), nativesFolder);
    }

}
