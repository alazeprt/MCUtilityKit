package top.alazeprt.version;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import top.alazeprt.storage.BasicStorageTest;

import java.io.File;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InstanceTest {

    public static Instance instance;

    @Test
    @Order(1)
    public void createTest() {
        instance.create(8);
    }

    @Test
    @Order(2)
    public void downloadAssetsTest() {
        instance.downloadAssets(8, 16);
    }

    @Test
    @Order(3)
    public void downloadLibrariesTest() {
        instance.downloadLibraries(4, 8);
    }

    static {
        instance = new Instance(new File(".minecraft"), VersionTest.version, "test");
        BasicStorageTest.basicStorage.addInstance(instance);
    }
}
