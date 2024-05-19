package com.alazeprt.minecraftutils.version;

public class VersionTest {

    static Version version;

    static {
        try {
            version = new Manifest().getVersionList().get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
