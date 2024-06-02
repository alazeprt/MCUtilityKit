package top.alazeprt.version;

import org.tinylog.Logger;
import top.alazeprt.util.HttpUtil;
import top.alazeprt.util.Result;
import top.alazeprt.util.ResultType;
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
    public Result<Manifest> reloadData() {
        Logger.info("Reloading manifest from " + url);
        Gson gson = new Gson();
        String data = null;
        try {
            data = HttpUtil.sendGet(url, new HashMap<>(), new HashMap<>());
            this.manifest = gson.fromJson(data, Map.class);
        } catch (IOException | URISyntaxException | ParseException e) {
            Logger.error("Failed to load manifest from " + url, e);
            return new Result<>(ResultType.NETWORK_IO_EXCEPTION);
        } catch (JsonSyntaxException e) {
            Logger.error("Failed to parse manifest from " + url, e);
            return new Result<>(ResultType.JSON_SYNTAX_EXCEPTION.setData(data));
        }
        return new Result<>(ResultType.SUCCESS, this);
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
    public Result<List<Version>> getVersionList() {
        Logger.info("Getting version list...");
        List<Version> versionList = new ArrayList<>();
        try {
            List<Map<String, Object>> versions = (List<Map<String, Object>>) manifest.get("versions");
            for(Map<String, Object> version : versions) {
                Logger.debug("Got version: " + version.get("id"));
                String id = version.get("id").toString();
                VersionType type = VersionType.valueOf(version.get("type").toString().toUpperCase());
                String url = version.get("url").toString();
                String releaseTime = version.get("releaseTime").toString();
                Version version1 = new Version(id, type, url, releaseTime);
                versionList.add(version1);
            }
        } catch (NullPointerException e) {
            Logger.error("Failed to get version list", e);
            return new Result<>(ResultType.MANIFEST_ERROR.setData(manifest));
        }
        return new Result<>(ResultType.SUCCESS, versionList);
    }
}
