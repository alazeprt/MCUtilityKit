package com.alazeprt.minecraftutils.java;

public class Java {
    private final String path;
    private final int version;

    public Java(String path, int version) {
        this.path = path;
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public int getVersion() {
        return version;
    }
}
