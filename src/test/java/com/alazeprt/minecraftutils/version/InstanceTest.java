package com.alazeprt.minecraftutils.version;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;

import static com.alazeprt.minecraftutils.version.VersionTest.version;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InstanceTest {

    public static Instance instance = new Instance(version, "Test_" + version.version());

    @Test
    @Order(1)
    public void createTest() throws Exception {
        instance.create(new File(".minecraft"), 8);
    }

    @Test
    @Order(2)
    public void downloadAssetsTest() throws Exception {
        instance.downloadAssets(new File(".minecraft"), 8, 16);
    }

    @Test
    @Order(3)
    public void downloadLibrariesTest() throws Exception {
        instance.downloadLibraries(new File(".minecraft"), 4, 8);
    }
}
