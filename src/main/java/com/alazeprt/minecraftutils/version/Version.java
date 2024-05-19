package com.alazeprt.minecraftutils.version;

import com.alazeprt.minecraftutils.util.DownloadUtil;
import com.alazeprt.minecraftutils.util.HttpUtil;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
