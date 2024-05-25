package top.alazeprt.account;

import top.alazeprt.util.HttpUtil;
import top.alazeprt.util.Result;
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

public class MicrosoftAccount implements Account {
    private final AccountType type = AccountType.MICROSOFT;

    private String name;

    private final UUID uuid;

    private String access_token;

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

    public String getAccess_token() {
        return access_token;
    }

    public static Result openLogin() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(URI.create("https://login.live.com/oauth20_authorize.srf?client_id=00000000402b5328&response_type=code&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf"));
        } catch (IOException e) {
            return Result.NETWORK_IO_EXCEPTION.setData(e);
        } catch (UnsupportedOperationException e) {
            return Result.UNSUPPORTED_EXCEPTION.setData(e);
        }
        return Result.SUCCESS;
    }

    public static Result verifyLogin(String url) {
        Gson gson = new Gson();
        String[] arguments = url.split("\\?")[1].split("&");
        Map<String, String> map = new HashMap<>();
        for (String argument: arguments) {
            String[] pair = argument.split("=");
            if(pair.length == 2) {
                map.put(pair[0], pair[1]);
            }
        }
        if(!map.containsKey("code")) {
            return Result.URL_PARAMETER_NOT_FOUND.setData(url);
        }
        Map<String, Object> microsoftSend = Map.of(
                "client_id", "00000000402b5328", 
                "code", map.get("code"), 
                "grant_type", "authorization_code",
                "redirect_uri", "https://login.live.com/oauth20_desktop.srf", 
                "scope", "service::user.auth.xboxlive.com::MBI_SSL"
                );
        try {
            String microsoftData = HttpUtil.sendPost("https://login.live.com/oauth20_token.srf", microsoftSend, new HashMap<>(), true);
            Map<String, Object> microsoftMap = (Map<String, Object>) gson.fromJson(microsoftData, Map.class);
            if(Objects.equals(microsoftMap.get("access_token"), null)) {
                return Result.MICROSOFT_TOKEN_NOT_FOUND.setData(microsoftMap);
            }
            Map<String, Object> xBoxSend = Map.of(
                    "Properties", Map.of("AuthMethod", "RPS", "SiteName", "user.auth.xboxlive.com", "RpsTicket", microsoftMap.get("access_token").toString()),
                    "RelyingParty", "http://auth.xboxlive.com",
                    "TokenType", "JWT"
            );
            String xBoxData = HttpUtil.sendPost("https://user.auth.xboxlive.com/user/authenticate", xBoxSend, Map.of(
                    "Content-Type", "application/json",
                    "Accept", "application/json"
            ), false);
            Map<String, Object> xBoxMap = (Map<String, Object>) gson.fromJson(xBoxData, Map.class);
            if(!xBoxMap.containsKey("Token") || !xBoxMap.containsKey("DisplayClaims")) {
                return Result.XBOX_LIVE_TOKEN_NOT_FOUND.setData(xBoxMap);
            }
            String uhs = ((Map<?, ?>)((List<?>)((Map<?, ?>) xBoxMap.get("DisplayClaims")).get("xui")).get(0)).get("uhs").toString();
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
                return Result.XSTS_TOKEN_NOT_FOUND.setData(xstsMap);
            }
            String minecraftData = HttpUtil.sendPost("https://api.minecraftservices.com/launcher/login", Map.of("xtoken", "XBL3.0 x=" + uhs + ";" + xstsMap.get("Token").toString()), Map.of("Content-Type", "application/json"), false);
            Map<String, Object> minecraftMap = (Map<String, Object>) gson.fromJson(minecraftData, Map.class);
            if(!minecraftMap.containsKey("access_token")) {
                return Result.MICROSOFT_ACCESS_TOKEN_NOT_FOUND.setData(minecraftMap);
            }
            String access_token = minecraftMap.get("access_token").toString();
            String checkData = HttpUtil.sendGet("https://api.minecraftservices.com/entitlements/mcstore", new HashMap<>(), Map.of("Authorization", "Bearer " + access_token));
            Map<String, Object> checkMap = (Map<String, Object>) gson.fromJson(checkData, Map.class);
            if(!checkMap.containsKey("items")) {
                return Result.NOT_OWN_MINECRAFT.setData(checkMap);
            }
            String profileData = HttpUtil.sendGet("https://api.minecraftservices.com/minecraft/profile", new HashMap<>(), Map.of("Authorization", "Bearer " + access_token));
            Map<String, Object> profileMap = (Map<String, Object>) gson.fromJson(profileData, Map.class);
            if(!profileMap.containsKey("id")) {
                return Result.INVALID_PROFILE.setData(profileMap);
            }
            UUID uuid = UUIDUtil.formatUuid(profileMap.get("id").toString());
            String name = profileMap.get("name").toString();
            return Result.SUCCESS.setData(new MicrosoftAccount(name, uuid, access_token));
        } catch (IOException | ParseException | URISyntaxException e) {
            return Result.NETWORK_IO_EXCEPTION;
        } catch (JsonSyntaxException e) {
            return Result.JSON_SYNTAX_EXCEPTION.setData(e);
        }
    }
}
