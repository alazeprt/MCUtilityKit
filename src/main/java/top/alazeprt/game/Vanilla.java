package top.alazeprt.game;

import top.alazeprt.account.Account;
import top.alazeprt.account.MicrosoftAccount;
import top.alazeprt.java.Java;
import top.alazeprt.util.FileUtil;
import top.alazeprt.util.Result;
import top.alazeprt.util.UUIDUtil;
import top.alazeprt.version.Instance;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a vanilla game operation
 *
 * @author alazeprt
 * @version 1.1
 */
public class Vanilla {

    /**
     * Extract native libraries
     *
     * @param instance the instance
     * @return the result of the operation
     */
    public static Result extractNatives(Instance instance) {
        File root = instance.root();
        Gson gson = new Gson();
        Map<String, Object> json = null;
        try {
            json = gson.fromJson(new InputStreamReader(
                    new FileInputStream(new File(root, "versions/" + instance.name() + "/" + instance.name() + ".json")), StandardCharsets.UTF_8), Map.class);
        } catch (FileNotFoundException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        File librariesFolder = new File(root, "libraries");
        if(!librariesFolder.exists()) {
            librariesFolder.mkdirs();
        }
        File versionFolder = new File(root, "versions/" + instance.name());
        if (!versionFolder.exists()) {
            versionFolder.mkdirs();
        }
        List<Map<String, Object>> libraries = (List<Map<String, Object>>) json.get("libraries");
        String folderName = "";
        for(Map<String, Object> library : libraries) {
            String name = library.get("name").toString();
            String path = ((Map<String, Object>)((Map<String, Object>) library.get("downloads")).get("artifact")).get("path").toString();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            try {
                if(name.contains("native")) {
                    if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                        if(System.getProperty("os.arch").toLowerCase().contains("64")) {
                            folderName = "natives-windows-x86_64";
                            if(!fileName.contains("windows.jar")) {
                                continue;
                            }
                            File nativesFolder = new File(versionFolder, folderName);
                            nativesFolder.mkdirs();
                            FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".dll");
                        } else if(System.getProperty("os.arch").toLowerCase().contains("86")) {
                            folderName = "natives-windows-x86";
                            if(!fileName.contains("windows-x86.jar")) {
                                continue;
                            }
                            File nativesFolder = new File(versionFolder, folderName);
                            nativesFolder.mkdirs();
                            FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".dll");
                        } else if(System.getProperty("os.arch").toLowerCase().contains("arm")) {
                            folderName = "natives-windows-arm64";
                            if(!fileName.contains("windows-arm64.jar")) {
                                continue;
                            }
                            File nativesFolder = new File(versionFolder, folderName);
                            nativesFolder.mkdirs();
                            FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".dll");
                        }
                    } else if(System.getProperty("os.name").toLowerCase().contains("linux")) {
                        folderName = "natives-linux-x86_64";
                        if(!fileName.contains("linux")) {
                            continue;
                        }
                        File nativesFolder = new File(versionFolder, folderName);
                        nativesFolder.mkdirs();
                        FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".so");
                    } else if(System.getProperty("os.name").toLowerCase().contains("mac")) {
                        folderName = "natives-macos-x86_64";
                        if(System.getProperty("os.arch").toLowerCase().contains("arm")) {
                            if(!fileName.contains("macos-arm64.jar")) {
                                continue;
                            }
                            File nativesFolder = new File(versionFolder, folderName);
                            nativesFolder.mkdirs();
                            FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".dylib");
                        } else if(System.getProperty("os.arch").toLowerCase().contains("64")) {
                            folderName = "natives-macos-x86_64";
                            if(!fileName.contains("macos.jar")) {
                                continue;
                            }
                            File nativesFolder = new File(versionFolder, folderName);
                            nativesFolder.mkdirs();
                            FileUtil.searchAndExtractFiles(librariesFolder.getAbsolutePath() + File.separator + path, nativesFolder.getAbsolutePath(), ".dylib");
                        }
                    }
                }
            } catch (IOException e) {
               return Result.FILE_IO_EXCEPTION.setData(e);
            }
        }
        return Result.SUCCESS.setData(new File(versionFolder, folderName).getAbsolutePath());
    }

    /**
     * Launch the game
     *
     * @param java the java
     * @param instance the instance
     * @param account the account
     * @param nativesFolder the natives folder
     * @return the result of the operation
     */
    public static Result launch(Java java, Instance instance, Account account, String nativesFolder) {
        File root = instance.root();
        Gson gson = new Gson();
        Map<String, Object> json = null;
        try {
            json = gson.fromJson(new InputStreamReader(
                    new FileInputStream(new File(root, "versions/" + instance.name() + "/" + instance.name() + ".json")), StandardCharsets.UTF_8), Map.class);
        } catch (FileNotFoundException e) {
            return Result.FILE_IO_EXCEPTION.setData(e);
        }
        File libraryFolder = new File(root, "libraries");
        List<Map<String, Map<String, Map<String, Object>>>> libraries = (List<Map<String, Map<String, Map<String, Object>>>>)
                json.get("libraries");
        StringBuilder librariesString = new StringBuilder();
        for(Map<String, Map<String, Map<String, Object>>> library : libraries) {
            String fileName = library.get("downloads").get("artifact").get("path").toString();
            if(fileName.contains("native")) {
                if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                    if(System.getProperty("os.arch").toLowerCase().contains("64")) {
                        if(!fileName.contains("windows.jar")) {
                            continue;
                        }
                    } else if(System.getProperty("os.arch").toLowerCase().contains("86")) {
                        if(!fileName.contains("windows-x86.jar")) {
                            continue;
                        }
                    } else if(System.getProperty("os.arch").toLowerCase().contains("arm")) {
                        if(!fileName.contains("windows-arm64.jar")) {
                            continue;
                        }
                    }
                } else if(System.getProperty("os.name").toLowerCase().contains("linux")) {
                    if(!fileName.contains("linux")) {
                        continue;
                    }
                } else if(System.getProperty("os.name").toLowerCase().contains("mac")) {
                    if(System.getProperty("os.arch").toLowerCase().contains("arm")) {
                        if(!fileName.contains("macos-arm64.jar")) {
                            continue;
                        }
                    } else if(System.getProperty("os.arch").toLowerCase().contains("64")) {
                        if(!fileName.contains("macos.jar")) {
                            continue;
                        }
                    }
                }
            }
            librariesString.append(libraryFolder.getAbsolutePath() + File.separator +
                    library.get("downloads").get("artifact").get("path").toString().replace("/", "\\")).append(";");
        }
        librariesString.append(new File(root, "versions" + File.separator + instance.name() + File.separator + instance.name() + ".jar").getAbsolutePath());
        ArgumentHandler handler = new ArgumentHandler(root.getAbsolutePath(), nativesFolder, json.get("assets").toString(), librariesString.toString(),
                instance, account);
        List<Object> jvmArguments = (List<Object>) ((Map<String, Object>) json.get("arguments")).get("jvm");
        StringBuilder jvmArgumentsBuilder = new StringBuilder();
        for(Object jvmArgument : jvmArguments) {
            if(jvmArgument instanceof String) {
                jvmArgumentsBuilder.append(handler.handle(jvmArgument.toString())).append(" ");
            } else {
                Map<String, Object> jvmArgumentMap = (Map<String, Object>) jvmArgument;
                Map<String, String> os = (((List<Map<String, Map<String, String>>>) jvmArgumentMap.get("rules")).get(0)).get("os");
                if(os.containsKey("name")) {
                    if(!System.getProperty("os.name").toLowerCase().contains(os.get("name"))) {
                        continue;
                    }
                }
                if(os.containsKey("version")) {
                    Pattern pattern = Pattern.compile(os.get("version"));
                    Matcher matcher = pattern.matcher(System.getProperty("os.version"));
                    if(!matcher.matches()) {
                        continue;
                    }
                }
                if(os.containsKey("arch")) {
                    if(!System.getProperty("os.arch").toLowerCase().contains(os.get("arch"))) {
                        continue;
                    }
                }
                if(jvmArgumentMap.get("value") instanceof String string) {
                    jvmArgumentsBuilder.append(handler.handle(string)).append(" ");
                } else if(jvmArgumentMap.get("value") instanceof List list) {
                    for(Object string : list) {
                        jvmArgumentsBuilder.append(handler.handle(string.toString())).append(" ");
                    }
                }
            }
        }
        List<Object> gameArguments = (List<Object>) ((Map<String, Object>) json.get("arguments")).get("game");
        StringBuilder gameArgumentsBuilder = new StringBuilder();
        for(Object gameArgument : gameArguments) {
            if(gameArgument instanceof String) {
                gameArgumentsBuilder.append(handler.handle(gameArgument.toString())).append(" ");
            }
        }
        gameArgumentsBuilder.deleteCharAt(gameArgumentsBuilder.length() - 1);
        gameArgumentsBuilder.append("--width 854 --height 480 ");
        String command = "\"" + java.getPath().replace("\\", "\\\\") + "\" " + jvmArgumentsBuilder + json.get("mainClass").toString() + " " + gameArgumentsBuilder;
        Thread thread = new Thread(() -> {
            try {
                String[] commandArray = command.split(" ");

                ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
                processBuilder.directory(new File(root, "versions/" + instance.name()));
                Process process = processBuilder.start();
                process.waitFor();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        return Result.SUCCESS;
    }

    static class ArgumentHandler {

        private final String nativesFolder;

        private final String libraries;

        private final String assetIndex;

        private final String assetFolder;

        private final String gameFolder;

        private final String versionType;

        private final Account account;

        private final Instance instance;

        private final String launcherName;

        private final String launcherVersion;

        public ArgumentHandler(String root, String nativesFolder, String assetIndex, String libraries, Instance instance, Account account) {
            this.nativesFolder = nativesFolder;
            this.libraries = libraries;
            this.assetIndex = assetIndex;
            this.assetFolder = root + File.separator + "assets";
            this.gameFolder = root + File.separator + "versions" + File.separator + instance.name();
            this.account = account;
            this.instance = instance;
            this.launcherName = "MCUtilityKit";
            this.launcherVersion = "1.1";
            this.versionType = launcherName + " " + launcherVersion;
        }

        public String handle(String argument) {
            String access_token;
            if(account instanceof MicrosoftAccount) {
                access_token = ((MicrosoftAccount) account).getAccess_token();
            } else {
                access_token = UUIDUtil.unformatUuid(account.getUuid());
            }
            return argument.replace("${auth_player_name}", account.getName())
                    .replace("${version_name}", "\"" + instance.name() + "\"")
                    .replace("${game_directory}", gameFolder)
                    .replace("${assets_root}", assetFolder)
                    .replace("${assets_index_name}", assetIndex)
                    .replace("${auth_uuid}", UUIDUtil.unformatUuid(account.getUuid()))
                    .replace("${auth_access_token}", access_token)
                    .replace("${user_type}", "msa")
                    .replace("${version_type}", versionType)
                    .replace("${natives_directory}", nativesFolder)
                    .replace("${launcher_name}", launcherName)
                    .replace("${launcher_version}", launcherVersion)
                    .replace("${classpath}", libraries);
        }
    }
}
