package top.alazeprt.storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import top.alazeprt.account.Account;
import top.alazeprt.account.MicrosoftAccount;
import top.alazeprt.account.OfflineAccount;
import top.alazeprt.util.Result;
import top.alazeprt.version.Instance;
import top.alazeprt.version.Manifest;
import top.alazeprt.version.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Represents the default data store
 *
 * @author alazeprt
 * @version 1.1
 */
public class BasicStorage {

    private final File file;

    /**
     * Constructor for BasicStorage
     * Data is stored in .mcutilitykit.json by default
     */
    public BasicStorage() {
        this.file = new File(".mcutilitykit.json");
    }

    /**
     * Constructor for BasicStorage
     *
     * @param file the file to store the data
     */
    public BasicStorage(File file) {
        this.file = file;
    }

    /**
     * Create the data file
     *
     * @return the result of the operation
     */
    public Result create() {
        Gson gson = new Gson();
        if(file.exists()) {
            try {
                FileUtils.delete(file);
            } catch (IOException e) {
                return Result.FILE_IO_EXCEPTION.setData(e);
            }
        }
        try {
            file.createNewFile();
            FileUtils.writeStringToFile(file, gson.toJson(Map.of("version", 1)), StandardCharsets.UTF_8);
            Runtime.getRuntime().exec("attrib +H \"" + file.getAbsolutePath() + "\"");
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        return Result.SUCCESS;
    }

    /**
     * Save an account to the data file
     *
     * @param account the account to save
     * @return the result of the operation
     */
    public Result saveAccount(Account account) {
        Gson gson = new Gson();
        if(!file.exists()) {
            return Result.CONFIGURATION_NOT_FOUND;
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        } catch (JsonSyntaxException e) {
            return Result.JSON_SYNTAX_EXCEPTION.setData(e);
        }
        List<Map<String, Object>> accounts = (List<Map<String, Object>>) json.getOrDefault("accounts", new ArrayList<>());
        if(account instanceof MicrosoftAccount microsoftAccount) {
            accounts.add(Map.of("name", account.getName(), "type", "microsoft", "uuid", account.getUuid(),
                    "access_token", microsoftAccount.getAccess_token()));
        } else {
            accounts.add(Map.of("name", account.getName(), "type", "offline", "uuid", account.getUuid()));
        }
        json.put("accounts", accounts);
        try {
            FileUtils.writeStringToFile(file, gson.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        return Result.SUCCESS;
    }

    /**
     * Get an account from the data file
     *
     * @param name the name of the account
     * @return the result of the operation
     */
    public Result getAccount(String name) {
        Gson gson = new Gson();
        if(!file.exists()) {
            return Result.CONFIGURATION_NOT_FOUND;
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        } catch (JsonSyntaxException e) {
            return Result.JSON_SYNTAX_EXCEPTION.setData(e);
        }
        List<Map<String, Object>> accounts = (List<Map<String, Object>>) json.getOrDefault("accounts", new ArrayList<>());
        for(Map<String, Object> account : accounts) {
            if(account.get("name").equals(name)) {
                String type = (String) account.get("type");
                String uuid = (String) account.get("uuid");
                if(type.equals("microsoft")) {
                    String access_token = (String) account.get("access_token");
                    return Result.SUCCESS.setData(new MicrosoftAccount(name, UUID.fromString(uuid), access_token));
                } else {
                    return Result.SUCCESS.setData(new OfflineAccount(name, UUID.fromString(uuid)));
                }
            }
        }
        return Result.ACCOUNT_NOT_FOUND;
    }

    /**
     * Remove an account from the data file
     *
     * @param name the name of the account
     * @return the result of the operation
     */
    public Result removeAccount(String name) {
        Gson gson = new Gson();
        if(!file.exists()) {
            return Result.CONFIGURATION_NOT_FOUND;
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        } catch (JsonSyntaxException e) {
            return Result.JSON_SYNTAX_EXCEPTION.setData(e);
        }
        List<Map<String, Object>> accounts = (List<Map<String, Object>>) json.getOrDefault("accounts", new ArrayList<>());
        for(Map<String, Object> account : accounts) {
            if(account.get("name").equals(name)) {
                accounts.remove(account);
                break;
            }
        }
        json.put("accounts", accounts);
        try {
            FileUtils.writeStringToFile(file, gson.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        return Result.SUCCESS;
    }

    /**
     * Add an instance to the data file
     *
     * @param instance the instance to add
     * @return the result of the operation
     */
    public Result addInstance(Instance instance) {
        Gson gson = new Gson();
        if(!file.exists()) {
            return Result.CONFIGURATION_NOT_FOUND;
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        } catch (JsonSyntaxException e) {
            return Result.JSON_SYNTAX_EXCEPTION.setData(e);
        }
        List<Map<String, Object>> instances = (List<Map<String, Object>>) json.getOrDefault("instances", new ArrayList<>());
        instances.add(Map.of("name", instance.name(), "version", instance.version().version(), "root", instance.root().getAbsolutePath()));
        json.put("instances", instances);
        try {
            FileUtils.writeStringToFile(file, gson.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        return Result.SUCCESS;
    }

    /**
     * Get all instances from the data file
     *
     * @param manifest the manifest that used to construct version information
     * @return the result of the operation
     */
    public Result getInstances(Manifest manifest) {
        Gson gson = new Gson();
        if(!file.exists()) {
            return Result.CONFIGURATION_NOT_FOUND;
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        } catch (JsonSyntaxException e) {
            return Result.JSON_SYNTAX_EXCEPTION.setData(e);
        }
        List<Map<String, Object>> instances = (List<Map<String, Object>>) json.getOrDefault("instances", new ArrayList<>());
        List<Instance> instanceList = new ArrayList<>();
        Map<String, Version> versionMap = new HashMap<>();
        Result result = manifest.getVersionList();
        if(!result.equals(Result.SUCCESS)) {
            return result;
        }
        List<Version> versionList = (List<Version>) result.getData();
        for(Version version : versionList) {
            versionMap.put(version.version(), version);
        }
        for(Map<String, Object> instance : instances) {
            if(!versionMap.containsKey(instance.get("version").toString())) {
                return Result.INSTANCE_NOT_FOUND.setData(instance.get("version").toString());
            }
            instanceList.add(new Instance(new File(instance.get("root").toString()), versionMap.get(instance.get("version")), instance.get("name").toString()));
        }
        return Result.SUCCESS.setData(instanceList);
    }

    /**
     * Remove an instance from the data file
     *
     * @param name the name of the instance
     * @return the result of the operation
     */
    public Result removeInstance(String name) {
        Gson gson = new Gson();
        if (!file.exists()) {
            return Result.CONFIGURATION_NOT_FOUND;
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        } catch (JsonSyntaxException e) {
            return Result.JSON_SYNTAX_EXCEPTION.setData(e);
        }
        List<Map<String, Object>> instances = (List<Map<String, Object>>) json.getOrDefault("instances", new ArrayList<>());
        for (Map<String, Object> instance : instances) {
            if (instance.get("name").equals(name)) {
                instances.remove(instance);
                break;
            }
        }
        json.put("instances", instances);
        try {
            FileUtils.writeStringToFile(file, gson.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        return Result.SUCCESS;
    }
}
