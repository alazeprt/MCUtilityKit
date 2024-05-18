package com.alazeprt.minecraftutils.version;

public record Version(String version, VersionType type, String url, String releaseTime) {
    @Override
    public String toString() {
        return "Version{" +
                "version='" + version + '\'' +
                ", type=" + type +
                ", url='" + url + '\'' +
                ", releaseTime='" + releaseTime + '\'' +
                '}';
    }
}
