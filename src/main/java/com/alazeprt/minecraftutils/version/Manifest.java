package com.alazeprt.minecraftutils.version;

import com.alazeprt.minecraftutils.util.HttpUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manifest {

    private String url = "https://piston-meta.mojang.com/mc/game/version_manifest.json";

    private Map<String, Object> manifest;

    public Manifest() throws Exception {
        reloadData();
    }

    public Manifest(String url) throws Exception {
        this.url = url;
        reloadData();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void reloadData() throws Exception {
        Gson gson = new Gson();
        String data = HttpUtil.sendGet(url, new HashMap<>(), new HashMap<>());
        this.manifest = gson.fromJson(data, Map.class);
    }

    public Map<String, Object> getManifest() {
        return manifest;
    }

    public List<Version> getVersionList() {
        List<Version> versionList = new ArrayList<>();
        List<Map<String, Object>> versions = (List<Map<String, Object>>) manifest.get("versions");
        for(Map<String, Object> version : versions) {
            String id = version.get("id").toString();
            VersionType type = VersionType.valueOf(version.get("type").toString().toUpperCase());
            String url = version.get("url").toString();
            String releaseTime = version.get("releaseTime").toString();
            Version version1 = new Version(id, type, url, releaseTime);
            versionList.add(version1);
        }
        return versionList;
    }
}
