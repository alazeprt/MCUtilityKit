package com.alazeprt.minecraftutils.version;

import org.junit.jupiter.api.Test;

import java.io.File;

public class VersionTest {
    @Test
    public void createTest() throws Exception {
        Manifest manifest = new Manifest();
        Version version = manifest.getVersionList().get(0);
        version.create(new File(".minecraft"));
    }
}
