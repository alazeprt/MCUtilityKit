package top.alazeprt.version;

import top.alazeprt.util.HttpUtil;
import top.alazeprt.util.Result;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Represents a manifest
 *
 * @author alazeprt
 * @version 1.1
 */
public class Manifest {

    private String url = "https://piston-meta.mojang.com/mc/game/version_manifest.json";

    private Map<String, Object> manifest;

    /**
     * Constructor for a manifest
     */
    public Manifest() {

    }

    /**
     * Constructor for a manifest
     *
     * @param url the url of the manifest
     */
    public Manifest(String url) {
        this.url = url;
    }

    /**
     * Set the url of the manifest
     *
     * @param url the url of the manifest
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the url of the manifest
     *
     * @return the url of the manifest
     */
    public String getUrl() {
        return url;
    }

    /**
     * Reload the manifest from the url
     *
     * @return the result of the operation
     */
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

    /**
     * Get the manifest
     *
     * @return the manifest
     */
    public Map<String, Object> getManifest() {
        return manifest;
    }

    /**
     * Get the version list
     *
     * @return the version list
     */
    public Result getVersionList() {
        List<Version> versionList = new ArrayList<>();
        try {
            List<Map<String, Object>> versions = (List<Map<String, Object>>) manifest.get("versions");
            for(Map<String, Object> version : versions) {
                String id = version.get("id").toString();
                VersionType type = VersionType.valueOf(version.get("type").toString().toUpperCase());
                String url = version.get("url").toString();
                String releaseTime = version.get("releaseTime").toString();
                Version version1 = new Version(id, type, url, releaseTime);
                versionList.add(version1);
            }
        } catch (NullPointerException e) {
            return Result.MANIFEST_ERROR.setData(manifest);
        }
        return Result.SUCCESS.setData(versionList);
    }
}
