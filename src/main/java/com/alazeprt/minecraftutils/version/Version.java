package com.alazeprt.minecraftutils.version;

import com.alazeprt.minecraftutils.util.DownloadUtil;
import com.alazeprt.minecraftutils.util.HttpUtil;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

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

    public void create(File root) throws Exception {
        Gson gson = new Gson();
        File versionFolder = new File(root, "versions/" + version);
        if (!versionFolder.exists()) {
            System.out.println(1);
            versionFolder.mkdirs();
        }
        DownloadUtil.download(url, versionFolder.getAbsolutePath() + "/client.json", 8);
        Map<String, Object> json = gson.fromJson(new InputStreamReader(new FileInputStream(new File(versionFolder, "client.json"))), Map.class);
        Map<String, Object> clientInfo = ((Map<String, Object>)((Map<String, Object>) json.get("downloads")).get("client"));
        String clientUrl = clientInfo.get("url").toString();
        String clientsha1 = clientInfo.get("sha1").toString();
        DownloadUtil.download(clientUrl, versionFolder.getAbsolutePath() + "/client.jar", 8);
        if(!HttpUtil.sha1verify(new File(versionFolder, "client.jar"), clientsha1)) {
            DownloadUtil.download(clientUrl, versionFolder.getAbsolutePath() + "/client.jar", 1);
            if(!HttpUtil.sha1verify(new File(versionFolder, "client.jar"), clientsha1)) {
                throw new RuntimeException("The SHA-1 of the downloaded file does not match the SHA-1 of the original file!");
            }
        }
        FileUtils.moveFile(new File(versionFolder, "client.jar"), new File(versionFolder, version + ".jar"));
        FileUtils.moveFile(new File(versionFolder, "client.json"), new File(versionFolder, version + ".json"));
    }
}
