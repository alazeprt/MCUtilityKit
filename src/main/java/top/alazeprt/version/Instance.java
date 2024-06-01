package top.alazeprt.version;

import org.tinylog.Logger;
import top.alazeprt.util.DownloadUtil;
import top.alazeprt.util.HttpUtil;
import top.alazeprt.util.Result;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Represents an instance
 *
 * @author alazeprt
 * @version 1.1
 *
 * @param name the name of the instance
 * @param version the version of the instance
 * @param root the root folder of the instance
 */
public record Instance(File root, Version version, String name) {
    /**
     * Create an instance
     *
     * @param threadCount number of threads used to download client files
     * @return the result of the operation
     */
    public Result create(int threadCount) {
        Logger.info("Creating instance: " + name);
        Gson gson = new Gson();
        File versionFolder = new File(root, "versions/" + name);
        if (!versionFolder.exists()) {
            versionFolder.mkdirs();
        }
        if(new File(versionFolder, name + ".json").exists()) {
            try {
                FileUtils.delete(new File(versionFolder, name + ".json"));
            } catch (IOException e) {
                Logger.error("Failed to delete \"" + versionFolder + "/" + name + ".json\"", e);
                return Result.FILE_IO_EXCEPTION.setData(e);
            }
        }
        try {
            DownloadUtil.single(version.url(), versionFolder.getAbsolutePath() + "/client.json");
        } catch (IOException e) {
            Logger.error("Failed to download \"" + versionFolder + "/client.json\"", e);
            return Result.NETWORK_IO_EXCEPTION.setData(e);
        }
        Logger.info("Downloading client.json ...");
        String clientUrl;
        String clientSha1;
        try {
            InputStream i = new FileInputStream(new File(versionFolder, "client.json"));
            InputStreamReader reader = new InputStreamReader(i, StandardCharsets.UTF_8);
            Map<String, Object> json = gson.fromJson(reader, Map.class);
            reader.close();
            i.close();
            Map<String, Object> clientInfo = ((Map<String, Object>)((Map<String, Object>) json.get("downloads")).get("client"));
            clientUrl = clientInfo.get("url").toString();
            clientSha1 = clientInfo.get("sha1").toString();
            if(new File(versionFolder, name + ".jar").exists()) {
                try {
                    if(HttpUtil.sha1verify(new File(versionFolder, name + ".jar"), clientSha1)) {
                        FileUtils.moveFile(new File(versionFolder, "client.json"), new File(versionFolder, name + ".json"));
                        return Result.SUCCESS;
                    } else {
                        FileUtils.delete(new File(versionFolder, name + ".jar"));
                    }
                } catch (NoSuchAlgorithmException e) {
                    Logger.error("Failed to verify \"" + versionFolder + "/" + name + ".jar\"", e);
                    return Result.ALGORITHM_EXCEPTION.setData(e);
                }
            }
        } catch (IOException e) {
            Logger.error("Failed to read \"" + versionFolder + "/client.json\"", e);
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        Logger.info("Downloading client.jar ...");
        try {
            DownloadUtil.multi(clientUrl, versionFolder.getAbsolutePath() + "/client.jar", threadCount);
        } catch (InterruptedException | IOException e) {
            Logger.error("Failed to download \"" + versionFolder + "/client.jar\"", e);
            return Result.NETWORK_IO_EXCEPTION.setData(e);
        }
        try {
            if(!HttpUtil.sha1verify(new File(versionFolder, "client.jar"), clientSha1)) {
                try {
                    DownloadUtil.multi(clientUrl, versionFolder.getAbsolutePath() + "/client.jar", 1);
                } catch (IOException | InterruptedException e) {
                    Logger.error("Failed to download \"" + versionFolder + "/client.jar\"", e);
                    return Result.NETWORK_IO_EXCEPTION.setData(e);
                }
                if(!HttpUtil.sha1verify(new File(versionFolder, "client.jar"), clientSha1)) {
                    Logger.warn("The SHA-1 of the \"client.jar\" does not match the SHA-1 of the original file!");
                    return Result.SHA1_NOT_MATCH
                            .setData(new RuntimeException("The SHA-1 of the \"client.jar\" does not match the SHA-1 of the original file!"));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Failed to verify \"" + versionFolder + "/client.jar\"", e);
            return Result.ALGORITHM_EXCEPTION.setData(e);
        } catch (IOException e) {
            Logger.error("Failed to read \"" + versionFolder + "/client.jar\"", e);
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        Logger.info("Renaming client files...");
        try {
            FileUtils.moveFile(new File(versionFolder, "client.jar"), new File(versionFolder, name + ".jar"));
            FileUtils.moveFile(new File(versionFolder, "client.json"), new File(versionFolder, name + ".json"));
        } catch (IOException e) {
            Logger.error("Failed to rename \"" + versionFolder + "/client.jar\"", e);
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        Logger.info("Successfully created instance: " + name);
        return Result.SUCCESS;
    }

    /**
     * Download assets
     *
     * @param downloadThreadCount number of threads used in downloading the specified asset file
     * @param assetsThreadCount number of threads divided to download all asset files
     * @return the result of the operation
     */
    public Result downloadAssets(int downloadThreadCount, int assetsThreadCount) {
        Logger.info("Downloading assets...");
        Gson gson = new Gson();
        File versionFolder = new File(root, "versions/" + name);
        if (!versionFolder.exists()) {
            versionFolder.mkdirs();
        }
        if(!(new File(versionFolder.getAbsolutePath() + "/" + name + ".json")).exists()) {
            try {
                DownloadUtil.multi(version().url(), versionFolder.getAbsolutePath() + "/" + name + ".json", downloadThreadCount);
            } catch (InterruptedException | IOException e) {
                return Result.NETWORK_IO_EXCEPTION.setData(e);
            }
        }
        Logger.info("Downloading asset index...");
        File assetIndexFolder = new File(root, "assets/indexes");
        assetIndexFolder.mkdirs();
        Map<String, Object> json = null;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(new File(versionFolder, name + ".json"))), Map.class);
        } catch (FileNotFoundException e) {
            Logger.error("Failed to read \"" + versionFolder + "/" + name + ".json\"", e);
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        Map<String, Object> assetIndexInfo = (Map<String, Object>) json.get("assetIndex");
        String assetIndexUrl = assetIndexInfo.get("url").toString();
        String assetIndexsha1 = assetIndexInfo.get("sha1").toString();
        String assetIndexName = assetIndexUrl.split("/")[assetIndexUrl.split("/").length - 1];
        try {
            DownloadUtil.multi(assetIndexUrl, assetIndexFolder.getAbsolutePath() + "/" + assetIndexName, 8);
        } catch (InterruptedException | IOException e) {
            Logger.error("Failed to download \"" + assetIndexFolder + "/" + assetIndexName + "\"", e);
            return Result.NETWORK_IO_EXCEPTION.setData(e);
        }
        try {
            if(!HttpUtil.sha1verify(new File(assetIndexFolder, assetIndexName), assetIndexsha1)) {
                try {
                    DownloadUtil.single(assetIndexUrl, assetIndexFolder.getAbsolutePath() + "/" + assetIndexName);
                } catch (IOException e) {
                    return Result.FILE_IO_EXCEPTION.setData(e);
                }
                if(!HttpUtil.sha1verify(new File(assetIndexFolder, assetIndexName), assetIndexsha1)) {
                    Logger.warn("The SHA-1 of the assetIndex does not match the SHA-1 of the original file!");
                    throw new RuntimeException("The SHA-1 of the assetIndex does not match the SHA-1 of the original file!");
                }
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            Logger.error("Failed to verify \"" + assetIndexFolder + "/" + assetIndexName + "\"", e);
            return Result.ALGORITHM_EXCEPTION.setData(e);
        }
        Logger.info("Downloading assets...");
        File assetsFolder = new File(root, "assets/objects");
        assetsFolder.mkdirs();
        Map<String, Map<String, Map<String, Object>>> assetIndex = null;
        try {
            assetIndex = gson.fromJson(
                    new InputStreamReader(new FileInputStream(new File(assetIndexFolder, assetIndexName))), Map.class);
        } catch (FileNotFoundException e) {
            Logger.error("Failed to read \"" + assetIndexFolder + "/" + assetIndexName + "\"", e);
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        try {
            downloadAssets(assetsThreadCount, assetIndex, assetsFolder);
            Logger.info("Checking assets...");
            downloadAssets(4, assetIndex, assetsFolder);
        } catch (InterruptedException e) {
            Logger.error("Failed to download assets", e);
            return Result.NETWORK_IO_EXCEPTION.setData(e);
        }
        return Result.SUCCESS;
    }

    private void downloadAssets(int assetsThreadCount, Map<String, Map<String, Map<String, Object>>> assetIndex, File assetsFolder)
            throws InterruptedException, RuntimeException {
        ExecutorService executor = Executors.newFixedThreadPool(assetsThreadCount);
        for (Map.Entry<String, Map<String, Object>> asset : assetIndex.get("objects").entrySet()) {
            String assetHash = asset.getValue().get("hash").toString();
            Logger.debug("Downloading asset \"" + assetHash + "\" ...");
            long size = (long) Double.parseDouble(asset.getValue().get("size").toString());
            File assetFolder = new File(assetsFolder, assetHash.substring(0, 2));
            if (!assetFolder.exists()) {
                assetFolder.mkdirs();
            }
            if (new File(assetFolder, assetHash).exists()) {
                if (FileUtils.sizeOf(new File(assetFolder, assetHash)) == size) {
                    Logger.debug("Asset \"" + assetHash + "\" already exists, skipping");
                    continue;
                }
            }

            executor.submit(() -> {
                try {
                    DownloadUtil.single("https://resources.download.minecraft.net/" + assetHash.substring(0, 2) + "/" + assetHash,
                            assetFolder.getAbsolutePath() + "/" + assetHash);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    /**
     * Download libraries
     *
     * @param downloadThreadCount number of threads used in downloading the specified library file
     * @param librariesThreadCount number of threads used in downloading all libraries
     * @return the result of the operation
     */
    public Result downloadLibraries(int downloadThreadCount, int librariesThreadCount) {
        Logger.info("Downloading libraries...");
        Gson gson = new Gson();
        File librariesFolder = new File(root, "libraries");
        if(!librariesFolder.exists()) {
            librariesFolder.mkdirs();
        }
        File versionFolder = new File(root, "versions/" + name);
        if (!versionFolder.exists()) {
            versionFolder.mkdirs();
        }
        Logger.info("Getting library list...");
        List<Map<String, Map<String, Map<String, Object>>>> libraries = null;
        try {
            libraries = (List<Map<String, Map<String, Map<String, Object>>>>)
                    gson.fromJson(new InputStreamReader(new FileInputStream(new File(versionFolder, name + ".json"))), Map.class).get("libraries");
        } catch (FileNotFoundException e) {
            Logger.error("Failed to read \"" + versionFolder + "/" + name + ".json\"", e);
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        ExecutorService executor = Executors.newFixedThreadPool(librariesThreadCount);
        for(Map<String, Map<String, Map<String, Object>>> library : libraries) {
            String url = library.get("downloads").get("artifact").get("url").toString();
            String path = library.get("downloads").get("artifact").get("path").toString();
            Logger.debug("Downloading library \"" + path + "\" ...");
            StringBuilder folder = new StringBuilder();
            for(int i = 0; i <= path.split("/").length - 2; i++) {
                folder.append(path.split("/")[i]).append("/");
            }
            File libraryFolder = new File(librariesFolder, folder.toString());
            if(!libraryFolder.exists()) {
                libraryFolder.mkdirs();
            }
            if(new File(librariesFolder, path).exists()) {
                try {
                    if(HttpUtil.sha1verify(new File(librariesFolder, path), library.get("downloads").get("artifact").get("sha1").toString())) {
                        continue;
                    }
                } catch (NoSuchAlgorithmException | IOException e) {
                    return Result.ALGORITHM_EXCEPTION.setData(e);
                }
            }
            try {
                executor.submit(() -> {
                    try {
                        DownloadUtil.multi(url, librariesFolder + "/" + path, downloadThreadCount);
                        if(!HttpUtil.sha1verify(new File(librariesFolder, path), library.get("downloads").get("artifact").get("sha1").toString())) {
                            DownloadUtil.single(url, librariesFolder + "/" + path);
                            if(!HttpUtil.sha1verify(new File(librariesFolder, path), library.get("downloads").get("artifact").get("sha1").toString())) {
                                Logger.warn("The SHA-1 of the library does not match the SHA-1 of the original file!");
                                throw new RuntimeException("The SHA-1 of the library does not match the SHA-1 of the original file!");
                            }
                        }
                    } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
                        Logger.error("Failed to download \"" + librariesFolder + "/" + path + "\"", e);
                        throw new RuntimeException(e);
                    }
                });
            } catch (RuntimeException e) {
                Logger.error("Failed to download \"" + librariesFolder + "/" + path + "\"", e);
                return Result.INDETERMINATE.setData(e);
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Logger.error("Failed to download libraries", e);
            return Result.NETWORK_IO_EXCEPTION.setData(e);
        }
        Logger.info("Libraries downloaded");
        return Result.SUCCESS;
    }
}