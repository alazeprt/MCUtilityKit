package top.alazeprt.minecraftutils.version;

import top.alazeprt.minecraftutils.util.HttpUtil;
import top.alazeprt.minecraftutils.util.Result;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Manifest {

    private String url = "https://piston-meta.mojang.com/mc/game/version_manifest.json";

    private Map<String, Object> manifest;

    public Manifest() {

    }

    public Manifest(String url) {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Result reloadData() {
        Gson gson = new Gson();
        String data = null;
        try {
            data = HttpUtil.sendGet(url, new HashMap<>(), new HashMap<>());
            this.manifest = gson.fromJson(data, Map.class);
        } catch (IOException | URISyntaxException | ParseException e) {
            return Result.NETWORK_IO_EXCEPTION;
        } catch (JsonSyntaxException e) {
            return Result.JSON_SYNTAX_EXCEPTION.setData(data);
        }
        return Result.SUCCESS.setData(this);
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
