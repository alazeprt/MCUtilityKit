package top.alazeprt.minecraftutils.version;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InstanceTest {

    public static Instance instance = new Instance(VersionTest.version, "test");

    @Test
    @Order(1)
    public void createTest() {
        instance.create(new File(".minecraft"), 8);
    }

    @Test
    @Order(2)
    public void downloadAssetsTest() {
        instance.downloadAssets(new File(".minecraft"), 8, 16);
    }

    @Test
    @Order(3)
    public void downloadLibrariesTest() {
        instance.downloadLibraries(new File(".minecraft"), 4, 8);
    }
}
