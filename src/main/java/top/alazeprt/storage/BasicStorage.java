package top.alazeprt.storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.tinylog.Logger;
import top.alazeprt.account.Account;
import top.alazeprt.account.MicrosoftAccount;
import top.alazeprt.account.OfflineAccount;
import top.alazeprt.util.Result;
import top.alazeprt.util.ResultType;
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
    public Result<ObjectUtils.Null> create() {
        Logger.info("Creating data file...");
        Gson gson = new Gson();
        if(file.exists()) {
            try {
                FileUtils.delete(file);
            } catch (IOException e) {
                Logger.error("Failed to delete data file", e);
                return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
            }
        }
        try {
            file.createNewFile();
            Logger.debug("Writing basic data...");
            FileUtils.writeStringToFile(file, gson.toJson(Map.of("version", 1)), StandardCharsets.UTF_8);
            Logger.debug("Setting hidden attribute...");
            Runtime.getRuntime().exec("attrib +H \"" + file.getAbsolutePath() + "\"");
        } catch (IOException e) {
            Logger.error("Failed to create data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        }
        return new Result<>(ResultType.SUCCESS);
    }

    /**
     * Save an account to the data file
     *
     * @param account the account to save
     * @return the result of the operation
     */
    public Result saveAccount(Account account) {
        Logger.info("Saving account {}...", account.getName());
        Gson gson = new Gson();
        if(!file.exists()) {
            Logger.error("Failed to read data file");
            return new Result<>(ResultType.CONFIGURATION_NOT_FOUND);
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            Logger.error("Failed to read data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        } catch (JsonSyntaxException e) {
            Logger.error("Failed to parse data file", e);
            return new Result<>(ResultType.JSON_SYNTAX_EXCEPTION, e);
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
            Logger.error("Failed to write data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        }
        return new Result<>(ResultType.SUCCESS);
    }

    /**
     * Get an account from the data file
     *
     * @param name the name of the account
     * @return the result of the operation
     */
    public Result<Account> getAccount(String name) {
        Logger.info("Getting account {}...", name);
        Gson gson = new Gson();
        if(!file.exists()) {
            Logger.error("Failed to read data file");
            return new Result<>(ResultType.CONFIGURATION_NOT_FOUND);
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            Logger.error("Failed to read data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        } catch (JsonSyntaxException e) {
            Logger.error("Failed to parse data file", e);
            return new Result<>(ResultType.JSON_SYNTAX_EXCEPTION, e);
        }
        List<Map<String, Object>> accounts = (List<Map<String, Object>>) json.getOrDefault("accounts", new ArrayList<>());
        for(Map<String, Object> account : accounts) {
            if(account.get("name").equals(name)) {
                String type = (String) account.get("type");
                String uuid = (String) account.get("uuid");
                Logger.debug("Found account {} with type {} and uuid {}", name, type, uuid);
                if(type.equals("microsoft")) {
                    String access_token = (String) account.get("access_token");
                    return new Result<>(ResultType.SUCCESS, new MicrosoftAccount(name, UUID.fromString(uuid), access_token));
                } else {
                    return new Result<>(ResultType.SUCCESS, new OfflineAccount(name, UUID.fromString(uuid)));
                }
            }
        }
        Logger.warn("Failed to find account {}", name);
        return new Result<>(ResultType.ACCOUNT_NOT_FOUND);
    }

    /**
     * Remove an account from the data file
     *
     * @param name the name of the account
     * @return the result of the operation
     */
    public Result<ObjectUtils.Null> removeAccount(String name) {
        Logger.info("Removing account {}...", name);
        Gson gson = new Gson();
        if(!file.exists()) {
            Logger.error("Failed to read data file");
            return new Result<>(ResultType.CONFIGURATION_NOT_FOUND);
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            Logger.error("Failed to read data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        } catch (JsonSyntaxException e) {
            Logger.error("Failed to parse data file", e);
            return new Result<>(ResultType.JSON_SYNTAX_EXCEPTION, e);
        }
        List<Map<String, Object>> accounts = (List<Map<String, Object>>) json.getOrDefault("accounts", new ArrayList<>());
        for(Map<String, Object> account : accounts) {
            if(account.get("name").equals(name)) {
                Logger.debug("Removed account {} with type {} and uuid {}", name, account.get("type"), account.get("uuid"));
                accounts.remove(account);
                break;
            }
        }
        Logger.debug("Updating account data...");
        json.put("accounts", accounts);
        try {
            FileUtils.writeStringToFile(file, gson.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.error("Failed to write data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        }
        return new Result<>(ResultType.SUCCESS);
    }

    /**
     * Add an instance to the data file
     *
     * @param instance the instance to add
     * @return the result of the operation
     */
    public Result addInstance(Instance instance) {
        Logger.info("Adding instance {}...", instance.name());
        Gson gson = new Gson();
        if(!file.exists()) {
            Logger.error("Failed to read data file");
            return new Result<>(ResultType.CONFIGURATION_NOT_FOUND);
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            Logger.error("Failed to read data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        } catch (JsonSyntaxException e) {
            Logger.error("Failed to parse data file", e);
            return new Result<>(ResultType.JSON_SYNTAX_EXCEPTION, e);
        }
        List<Map<String, Object>> instances = (List<Map<String, Object>>) json.getOrDefault("instances", new ArrayList<>());
        instances.add(Map.of("name", instance.name(), "version", instance.version().version(), "root", instance.root().getAbsolutePath()));
        json.put("instances", instances);
        try {
            FileUtils.writeStringToFile(file, gson.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.error("Failed to write data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        }
        return new Result<>(ResultType.SUCCESS);
    }

    /**
     * Get all instances from the data file
     *
     * @param manifest the manifest that used to construct version information
     * @return the result of the operation
     */
    public Result<List<Instance>> getInstances(Manifest manifest) {
        Logger.info("Getting instances...");
        Gson gson = new Gson();
        if(!file.exists()) {
            Logger.error("Failed to read data file");
            return new Result<>(ResultType.CONFIGURATION_NOT_FOUND);
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            Logger.error("Failed to read data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        } catch (JsonSyntaxException e) {
            Logger.error("Failed to parse data file", e);
            return new Result<>(ResultType.JSON_SYNTAX_EXCEPTION, e);
        }
        List<Map<String, Object>> instances = (List<Map<String, Object>>) json.getOrDefault("instances", new ArrayList<>());
        List<Instance> instanceList = new ArrayList<>();
        Map<String, Version> versionMap = new HashMap<>();
        Result<List<Version>> result = manifest.getVersionList();
        if(!result.getType().equals(ResultType.SUCCESS)) {
            Logger.warn("Failed to get version list", result.getException());
            return new Result<>(result.getType(), result.getException());
        }
        List<Version> versionList = (List<Version>) result.getData();
        for(Version version : versionList) {
            versionMap.put(version.version(), version);
        }
        for(Map<String, Object> instance : instances) {
            if(!versionMap.containsKey(instance.get("version").toString())) {
                Logger.warn("Failed to get version {}", instance.get("version").toString());
                return new Result<>(ResultType.INSTANCE_NOT_FOUND, new Exception("Failed to get version " + instance.get("version").toString()));
            }
            Logger.debug("Found instance {} with version {} and root {}", instance.get("name"), instance.get("version"), instance.get("root"));
            instanceList.add(new Instance(new File(instance.get("root").toString()), versionMap.get(instance.get("version")), instance.get("name").toString()));
        }
        Logger.info("Found instances: {}", Arrays.toString(instanceList.toArray()));
        return new Result<>(ResultType.SUCCESS, instanceList);
    }

    /**
     * Remove an instance from the data file
     *
     * @param name the name of the instance
     * @return the result of the operation
     */
    public Result<ObjectUtils.Null> removeInstance(String name) {
        Logger.info("Removing instance {}...", name);
        Gson gson = new Gson();
        if (!file.exists()) {
            Logger.error("Failed to read data file");
            return new Result<>(ResultType.CONFIGURATION_NOT_FOUND);
        }
        Map<String, Object> json;
        try {
            json = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Map.class);
        } catch (IOException e) {
            Logger.error("Failed to read data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        } catch (JsonSyntaxException e) {
            Logger.error("Failed to parse data file", e);
            return new Result<>(ResultType.JSON_SYNTAX_EXCEPTION, e);
        }
        List<Map<String, Object>> instances = (List<Map<String, Object>>) json.getOrDefault("instances", new ArrayList<>());
        for (Map<String, Object> instance : instances) {
            if (instance.get("name").equals(name)) {
                Logger.debug("Removed instance {} with version {} and root {}", instance.get("name"), instance.get("version"), instance.get("root"));
                instances.remove(instance);
                break;
            }
        }
        Logger.debug("Updating instance data...");
        json.put("instances", instances);
        try {
            FileUtils.writeStringToFile(file, gson.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.error("Failed to write data file", e);
            return new Result<>(ResultType.FILE_IO_EXCEPTION, e);
        }
        return new Result<>(ResultType.SUCCESS);
    }
}
