package com.alazeprt.minecraftutils.version;

import org.junit.jupiter.api.Test;

import java.io.File;

public class VersionTest {
    @Test
    public void createTest() throws Exception {
        Manifest manifest = new Manifest();
        Version version = manifest.getVersionList().get(0);
        version.create(new File(".minecraft"), 8);
    }

    @Test
    public void downloadAssetsTest() throws Exception {
        Manifest manifest = new Manifest();
        Version version = manifest.getVersionList().get(0);
        version.downloadAssets(new File(".minecraft"), 8, 16);
    }

    @Test
    public void downloadLibrariesTest() throws Exception {
        Manifest manifest = new Manifest();
        Version version = manifest.getVersionList().get(0);
        version.downloadLibraries(new File(".minecraft"), 4, 8);
    }
}
