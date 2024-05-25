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

public class BasicStorage {

    private final File file;

    public BasicStorage() {
        this.file = new File(".mcutilitykit.json");
    }

    public BasicStorage(File file) {
        this.file = file;
    }

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
        instances.add(Map.of("name", instance.name(), "version", instance.version().version()));
        json.put("instances", instances);
        try {
            FileUtils.writeStringToFile(file, gson.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        return Result.SUCCESS;
    }

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
        for(Version version : manifest.getVersionList()) {
            versionMap.put(version.version(), version);
        }
        for(Map<String, Object> instance : instances) {
            if(!versionMap.containsKey(instance.get("version").toString())) {
                return Result.VERSION_NOT_FOUND.setData(instance.get("version").toString());
            }
            instanceList.add(new Instance(versionMap.get(instance.get("version")), instance.get("name").toString()));
        }
        return Result.SUCCESS.setData(instanceList);
    }

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
