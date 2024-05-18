package com.alazeprt.minecraftutils.game;

import com.alazeprt.minecraftutils.util.FileUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Vanilla {
    public static void extractNatives(String root, String version) throws Exception {
        Gson gson = new Gson();
        Map<String, Object> json = gson.fromJson(new InputStreamReader(
                new FileInputStream(new File(root, "versions/" + version + "/" + version + ".json")), StandardCharsets.UTF_8), Map.class);
        File librariesFolder = new File(root, "libraries");
        if(!librariesFolder.exists()) {
            librariesFolder.mkdirs();
        }
        File versionFolder = new File(root, "versions/" + version);
        if (!versionFolder.exists()) {
            versionFolder.mkdirs();
        }
        List<Map<String, Object>> libraries = (List<Map<String, Object>>) json.get("libraries");
        for(Map<String, Object> library : libraries) {
            String name = library.get("name").toString();
            String path = ((Map<String, Object>)((Map<String, Object>) library.get("downloads")).get("artifact")).get("path").toString();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            if(name.contains("native")) {
                if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                    if(System.getProperty("os.arch").toLowerCase().contains("64")) {
                        if(!fileName.contains("windows.jar")) {
                            continue;
                        }
                        File nativesFolder = new File(versionFolder, "natives-windows-x86_64");
                        nativesFolder.mkdirs();
                        FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".dll");
                    } else if(System.getProperty("os.arch").toLowerCase().contains("86")) {
                        if(!fileName.contains("windows-x86.jar")) {
                            continue;
                        }
                        File nativesFolder = new File(versionFolder, "natives-windows-x86");
                        nativesFolder.mkdirs();
                        FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".dll");
                    } else if(System.getProperty("os.arch").toLowerCase().contains("arm")) {
                        if(!fileName.contains("windows-arm64.jar")) {
                            continue;
                        }
                        File nativesFolder = new File(versionFolder, "natives-windows-arm64");
                        nativesFolder.mkdirs();
                        FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".dll");
                    }
                } else if(System.getProperty("os.name").toLowerCase().contains("linux")) {
                    if(!fileName.contains("linux")) {
                        continue;
                    }
                    File nativesFolder = new File(versionFolder, "natives-linux-x86_64");
                    nativesFolder.mkdirs();
                    FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".so");
                } else if(System.getProperty("os.name").toLowerCase().contains("mac")) {
                    if(System.getProperty("os.arch").toLowerCase().contains("arm")) {
                        if(!fileName.contains("macos-arm64.jar")) {
                            continue;
                        }
                        File nativesFolder = new File(versionFolder, "natives-macos-arm64");
                        nativesFolder.mkdirs();
                        FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".dylib");
                    } else if(System.getProperty("os.arch").toLowerCase().contains("64")) {
                        if(!fileName.contains("macos.jar")) {
                            continue;
                        }
                        File nativesFolder = new File(versionFolder, "natives-macos-x86_64");
                        nativesFolder.mkdirs();
                        FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".dylib");
                    }
                }
            }
        }
    }
}
