package top.alazeprt.account;

import org.apache.commons.lang3.ObjectUtils;
import org.tinylog.Logger;
import top.alazeprt.util.HttpUtil;
import top.alazeprt.util.Result;
import top.alazeprt.util.ResultType;
import top.alazeprt.util.UUIDUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.hc.core5.http.ParseException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;

/**
 * Represents a Microsoft account
 *
 * @author alazeprt
 * @version 1.1
 */
public class MicrosoftAccount implements Account {
    private final AccountType type = AccountType.MICROSOFT;

    private String name;

    private final UUID uuid;

    private String access_token;

    /**
     * Constructor for Microsoft account
     *
     * @param name the name of the account
     * @param uuid the uuid of the account
     * @param access_token the access token of the account
     */
    public MicrosoftAccount(String name, UUID uuid, String access_token) {
        this.name = name;
    	this.uuid = uuid;
	    this.access_token = access_token;
    }

    @Override
    public String getName() {
    	return name;
    }

    @Override
    public AccountType getType() {
        return type;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get access token of the account
     *
     * @return the access token
     */
    public String getAccess_token() {
        return access_token;
    }

    /**
     * Open the Microsoft account login page for the user
     *
     * @return the result of the operation
     */
    public static Result<ObjectUtils.Null> openLogin() {
        Logger.info("Opening Microsoft login page...");
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(URI.create("https://login.live.com/oauth20_authorize.srf?client_id=00000000402b5328&response_type=code&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf"));
        } catch (IOException e) {
            Logger.error("Failed to open Microsoft login page", e);
            return new Result<>(ResultType.NETWORK_IO_EXCEPTION, e);
        } catch (UnsupportedOperationException e) {
            Logger.error("Failed to open Microsoft login page", e);
            return new Result<>(ResultType.UNSUPPORTED_EXCEPTION, e);
        }
        Logger.info("Microsoft login page opened");
        return new Result<>(ResultType.SUCCESS);
    }

    /**
     * Verify the login of the Microsoft account
     *
     * @param url the url that get at the end of the login
     * @return the result of the operation
     */
    public static Result<MicrosoftAccount> verifyLogin(String url) {
        Logger.info("Getting Minecraft account information...");
        Gson gson = new Gson();
        Logger.info("Getting Microsoft account information...");
        String[] arguments = url.split("\\?")[1].split("&");
        Map<String, String> map = new HashMap<>();
        for (String argument: arguments) {
            String[] pair = argument.split("=");
            if(pair.length == 2) {
                map.put(pair[0], pair[1]);
            }
        }
        if(!map.containsKey("code")) {
            Logger.error("Unable to get Microsoft account information via given url" + url);
            return new Result<>(ResultType.URL_PARAMETER_NOT_FOUND,
                    new Exception("Unable to get Microsoft account information via given url" + url));
        }
        Map<String, Object> microsoftSend = Map.of(
                "client_id", "00000000402b5328", 
                "code", map.get("code"), 
                "grant_type", "authorization_code",
                "redirect_uri", "https://login.live.com/oauth20_desktop.srf", 
                "scope", "service::user.auth.xboxlive.com::MBI_SSL"
                );
        try {
            Logger.info("Obtaining authorization token...");
            String microsoftData = HttpUtil.sendPost("https://login.live.com/oauth20_token.srf", microsoftSend, new HashMap<>(), true);
            Map<String, Object> microsoftMap = (Map<String, Object>) gson.fromJson(microsoftData, Map.class);
            if(Objects.equals(microsoftMap.get("access_token"), null)) {
                Logger.error("Unable to get Microsoft account information" + microsoftData);
                return new Result<>(ResultType.MICROSOFT_TOKEN_NOT_FOUND, new Exception("Unable to get Microsoft account information" + microsoftData));
            }
            Logger.info("XBox Live authentication in progress...");
            Map<String, Object> xBoxSend = Map.of(
                    "Properties", Map.of("AuthMethod", "RPS", "SiteName", "user.auth.xboxlive.com", "RpsTicket", microsoftMap.get("access_token").toString()),
                    "RelyingParty", "https://auth.xboxlive.com",
                    "TokenType", "JWT"
            );
            String xBoxData = HttpUtil.sendPost("https://user.auth.xboxlive.com/user/authenticate", xBoxSend, Map.of(
                    "Content-Type", "application/json",
                    "Accept", "application/json"
            ), false);
            Map<String, Object> xBoxMap = (Map<String, Object>) gson.fromJson(xBoxData, Map.class);
            if(!xBoxMap.containsKey("Token") || !xBoxMap.containsKey("DisplayClaims")) {
                Logger.error("Unable to get XBox account information" + xBoxData);
                return new Result<>(ResultType.XBOX_LIVE_TOKEN_NOT_FOUND, new Exception("Unable to get XBox account information" + xBoxData));
            }
            String uhs = ((Map<?, ?>)((List<?>)((Map<?, ?>) xBoxMap.get("DisplayClaims")).get("xui")).get(0)).get("uhs").toString();
            Logger.info("XSTS authentication in progress...");
            Map<String, Object> xstsSend = Map.of(
                    "Properties", Map.of("SandboxId", "RETAIL", "UserTokens", List.of(xBoxMap.get("Token").toString())),
                    "RelyingParty", "rp://api.minecraftservices.com/",
                    "TokenType", "JWT"
            );
            String xstsData = HttpUtil.sendPost("https://xsts.auth.xboxlive.com/xsts/authorize", xstsSend, Map.of(
                    "Content-Type", "application/json",
                    "Accept", "application/json"
            ), false);
            Map<String, Object> xstsMap = (Map<String, Object>) gson.fromJson(xstsData, Map.class);
            if(!xstsMap.containsKey("Token")) {
                Logger.error("Unable to get XSTS token from " + xstsData);
                return new Result<>(ResultType.XSTS_TOKEN_NOT_FOUND, new Exception("Unable to get XSTS token from " + xstsData));
            }
            Logger.info("Minecraft authentication in progress...");
            String minecraftData = HttpUtil.sendPost("https://api.minecraftservices.com/launcher/login", Map.of("xtoken", "XBL3.0 x=" + uhs + ";" + xstsMap.get("Token").toString()), Map.of("Content-Type", "application/json"), false);
            Map<String, Object> minecraftMap = (Map<String, Object>) gson.fromJson(minecraftData, Map.class);
            if(!minecraftMap.containsKey("access_token")) {
                Logger.error("Unable to get Minecraft access token from " + minecraftData);
                return new Result<>(ResultType.MICROSOFT_ACCESS_TOKEN_NOT_FOUND, new Exception("Unable to get Minecraft access token from " + minecraftData));
            }
            String access_token = minecraftMap.get("access_token").toString();
            Logger.info("Obtaining Minecraft profile...");
            String checkData = HttpUtil.sendGet("https://api.minecraftservices.com/entitlements/mcstore", new HashMap<>(), Map.of("Authorization", "Bearer " + access_token));
            Map<String, Object> checkMap = (Map<String, Object>) gson.fromJson(checkData, Map.class);
            if(!checkMap.containsKey("items")) {
                Logger.error("No Minecraft purchases on this account", checkData);
                return new Result<>(ResultType.NOT_OWN_MINECRAFT, new Exception(""));
            }
            String profileData = HttpUtil.sendGet("https://api.minecraftservices.com/minecraft/profile", new HashMap<>(), Map.of("Authorization", "Bearer " + access_token));
            Map<String, Object> profileMap = (Map<String, Object>) gson.fromJson(profileData, Map.class);
            if(!profileMap.containsKey("id")) {
                Logger.error("Invalid Minecraft profile" + profileData);
                return new Result<>(ResultType.INVALID_PROFILE, new Exception("Invalid Minecraft profile" + profileData));
            }
            UUID uuid = UUIDUtil.formatUuid(profileMap.get("id").toString());
            String name = profileMap.get("name").toString();
            Logger.info("Minecraft Account Information: Name: " + name + " UUID: " + uuid);
            Logger.debug(name + "'s Access token: " + access_token);
            return new Result<>(ResultType.SUCCESS, new MicrosoftAccount(name, uuid, access_token));
        } catch (IOException | ParseException | URISyntaxException e) {
            Logger.error("Failed to get Minecraft account information", e);
            return new Result<>(ResultType.NETWORK_IO_EXCEPTION, e);
        } catch (JsonSyntaxException e) {
            Logger.error("Failed to get Minecraft account information", e);
            return new Result<>(ResultType.JSON_SYNTAX_EXCEPTION, e);
        }
    }
}
