package top.alazeprt.minecraftutils.game;

import top.alazeprt.minecraftutils.account.OfflineAccount;
import top.alazeprt.minecraftutils.java.Java;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;

import static top.alazeprt.minecraftutils.version.InstanceTest.instance;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VanillaTest {

    private static String nativesFolder;

    @Test
    @Order(1)
    public void extractNativesTest() {
        nativesFolder = (String) Vanilla.extractNatives(new File(".minecraft"), instance.name()).getData();
    }

    @Test
    @Order(2)
    public void launchTest() {
        Vanilla.launch(new Java("C:\\Program Files\\Zulu\\zulu-21\\bin\\java.exe", 21), new File(".minecraft"),
                instance, new OfflineAccount("test"), nativesFolder);
    }

}
