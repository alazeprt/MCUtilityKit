package com.alazeprt.minecraftutils.game;

import com.alazeprt.minecraftutils.account.OfflineAccount;
import com.alazeprt.minecraftutils.java.Java;
import com.alazeprt.minecraftutils.version.Manifest;
import com.alazeprt.minecraftutils.version.Version;
import org.junit.jupiter.api.*;

import java.io.File;

import static com.alazeprt.minecraftutils.version.InstanceTest.instance;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VanillaTest {

    private static String nativesFolder;

    @Test
    @Order(1)
    public void extractNativesTest() throws Exception {
        nativesFolder = Vanilla.extractNatives(new File(".minecraft"), instance.name());
    }

    @Test
    @Order(2)
    public void launchTest() throws Exception {
        Vanilla.launch(new Java("C:\\Program Files\\Zulu\\zulu-21\\bin\\java.exe", 21), new File(".minecraft"),
                instance, new OfflineAccount("test"), nativesFolder);
    }

}
