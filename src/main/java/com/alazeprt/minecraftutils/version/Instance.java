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

public record Instance(Version version, String name) {
    public void create(File root, int threadCount) throws Exception {
        Gson gson = new Gson();
        File versionFolder = new File(root, "versions/" + name);
        if (!versionFolder.exists()) {
            versionFolder.mkdirs();
        }
        if(new File(versionFolder, name + ".json").exists()) {
            FileUtils.delete(new File(versionFolder, name + ".json"));
        }
        DownloadUtil.single(version.url(), versionFolder.getAbsolutePath() + "/client.json");
        InputStream i = new FileInputStream(new File(versionFolder, "client.json"));
        InputStreamReader reader = new InputStreamReader(i, StandardCharsets.UTF_8);
        Map<String, Object> json = gson.fromJson(reader, Map.class);
        reader.close();
        i.close();
        Map<String, Object> clientInfo = ((Map<String, Object>)((Map<String, Object>) json.get("downloads")).get("client"));
        String clientUrl = clientInfo.get("url").toString();
        String clientsha1 = clientInfo.get("sha1").toString();
        if(new File(versionFolder, name + ".jar").exists()) {
            if(HttpUtil.sha1verify(new File(versionFolder, name + ".jar"), clientsha1)) {
                FileUtils.moveFile(new File(versionFolder, "client.json"), new File(versionFolder, name + ".json"));
                return;
            } else {
                FileUtils.delete(new File(versionFolder, name + ".jar"));
            }
        }
        DownloadUtil.multi(clientUrl, versionFolder.getAbsolutePath() + "/client.jar", threadCount);
        if(!HttpUtil.sha1verify(new File(versionFolder, "client.jar"), clientsha1)) {
            DownloadUtil.multi(clientUrl, versionFolder.getAbsolutePath() + "/client.jar", 1);
            if(!HttpUtil.sha1verify(new File(versionFolder, "client.jar"), clientsha1)) {
                throw new RuntimeException("The SHA-1 of the \"client.jar\" does not match the SHA-1 of the original file!");
            }
        }
        FileUtils.moveFile(new File(versionFolder, "client.jar"), new File(versionFolder, name + ".jar"));
        FileUtils.moveFile(new File(versionFolder, "client.json"), new File(versionFolder, name + ".json"));
    }

    public void downloadAssets(File root, int downloadThreadCount, int assetsThreadCount) throws Exception {
        Gson gson = new Gson();
        File versionFolder = new File(root, "versions/" + name);
        if (!versionFolder.exists()) {
            versionFolder.mkdirs();
        }
        if(!(new File(versionFolder.getAbsolutePath() + "/" + name + ".json")).exists()) {
            DownloadUtil.multi(version().url(), versionFolder.getAbsolutePath() + "/" + name + ".json", downloadThreadCount);
        }
        File assetIndexFolder = new File(root, "assets/indexes");
        assetIndexFolder.mkdirs();
        Map<String, Object> json = gson.fromJson(new InputStreamReader(new FileInputStream(new File(versionFolder, name + ".json"))), Map.class);
        Map<String, Object> assetIndexInfo = (Map<String, Object>) json.get("assetIndex");
        String assetIndexUrl = assetIndexInfo.get("url").toString();
        String assetIndexsha1 = assetIndexInfo.get("sha1").toString();
        String assetIndexName = assetIndexUrl.split("/")[assetIndexUrl.split("/").length - 1];
        DownloadUtil.multi(assetIndexUrl, assetIndexFolder.getAbsolutePath() + "/" + assetIndexName, 8);
        if(!HttpUtil.sha1verify(new File(assetIndexFolder, assetIndexName), assetIndexsha1)) {
            DownloadUtil.single(assetIndexUrl, assetIndexFolder.getAbsolutePath() + "/" + assetIndexName);
            if(!HttpUtil.sha1verify(new File(assetIndexFolder, assetIndexName), assetIndexsha1)) {
                throw new RuntimeException("The SHA-1 of the assetIndex does not match the SHA-1 of the original file!");
            }
        }
        File assetsFolder = new File(root, "assets/objects");
        assetsFolder.mkdirs();
        Map<String, Map<String, Map<String, Object>>> assetIndex = gson.fromJson(new InputStreamReader(new FileInputStream(new File(assetIndexFolder, assetIndexName))), Map.class);
        downloadAssets(assetsThreadCount, assetIndex, assetsFolder);
        System.out.println("Checking assets...");
        downloadAssets(4, assetIndex, assetsFolder);
    }

    private void downloadAssets(int assetsThreadCount, Map<String, Map<String, Map<String, Object>>> assetIndex, File assetsFolder) {
        ExecutorService executor = Executors.newFixedThreadPool(assetsThreadCount);
        for (Map.Entry<String, Map<String, Object>> asset : assetIndex.get("objects").entrySet()) {
            String assetHash = asset.getValue().get("hash").toString();
            long size = (long) Double.parseDouble(asset.getValue().get("size").toString());
            File assetFolder = new File(assetsFolder, assetHash.substring(0, 2));
            if (!assetFolder.exists()) {
                assetFolder.mkdirs();
            }
            if (new File(assetFolder, assetHash).exists()) {
                if (FileUtils.sizeOf(new File(assetFolder, assetHash)) == size) {
                    System.out.println("Asset " + assetHash + " already exists, skipping download");
                    continue;
                }
            }

            executor.submit(() -> {
                DownloadUtil.single("https://resources.download.minecraft.net/" + assetHash.substring(0, 2) + "/" + assetHash,
                        assetFolder.getAbsolutePath() + "/" + assetHash);
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadLibraries(File root, int downloadThreadCount, int librariesThreadCount) throws Exception {
        Gson gson = new Gson();
        File librariesFolder = new File(root, "libraries");
        if(!librariesFolder.exists()) {
            librariesFolder.mkdirs();
        }
        File versionFolder = new File(root, "versions/" + name);
        if (!versionFolder.exists()) {
            versionFolder.mkdirs();
        }
        List<Map<String, Map<String, Map<String, Object>>>> libraries = (List<Map<String, Map<String, Map<String, Object>>>>)
                gson.fromJson(new InputStreamReader(new FileInputStream(new File(versionFolder, name + ".json"))), Map.class).get("libraries");
        ExecutorService executor = Executors.newFixedThreadPool(librariesThreadCount);
        for(Map<String, Map<String, Map<String, Object>>> library : libraries) {
            String url = library.get("downloads").get("artifact").get("url").toString();
            String path = library.get("downloads").get("artifact").get("path").toString();
            StringBuilder folder = new StringBuilder();
            for(int i = 0; i <= path.split("/").length - 2; i++) {
                folder.append(path.split("/")[i]).append("/");
            }
            File libraryFolder = new File(librariesFolder, folder.toString());
            if(!libraryFolder.exists()) {
                libraryFolder.mkdirs();
            }
            if(new File(librariesFolder, path).exists()) {
                if(HttpUtil.sha1verify(new File(librariesFolder, path), library.get("sha1").toString())) {
                    continue;
                }
            }
            executor.submit(() -> {
                try {
                    DownloadUtil.multi(url, librariesFolder + "/" + path, downloadThreadCount);
                    if(!HttpUtil.sha1verify(new File(librariesFolder, path), library.get("sha1").toString())) {
                        DownloadUtil.single(url, librariesFolder + "/" + path);
                        if(!HttpUtil.sha1verify(new File(librariesFolder, path), library.get("sha1").toString())) {
                            throw new RuntimeException("The SHA-1 of the library does not match the SHA-1 of the original file!");
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}