package top.alazeprt.util;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Represents the result of an operation
 *
 * @author alazeprt
 * @version 1.1
 */
public enum ResultType {
    /**
     * the operation is successful
     */
    SUCCESS(0),
    /**
     * An I/O exception occurred when operating on the file
     */
    FILE_IO_EXCEPTION(100),
    /**
     * An I/O exception occurred when operating on the network
     */
    NETWORK_IO_EXCEPTION(101),
    /**
     * An algorithm exception occurred
     */
    ALGORITHM_EXCEPTION(102),
    /**
     * A JSON syntax exception occurred
     */
    JSON_SYNTAX_EXCEPTION(103),
    /**
     * The SHA1 is not match
     */
    SHA1_NOT_MATCH(104),
    /**
     * An indeterminate exception occurred
     */
    INDETERMINATE(105),
    /**
     * An unsupported operation exception occurred
     */
    UNSUPPORTED_EXCEPTION(106),
    /**
     * The url returned by the login does not contain the required parameters
     */
    URL_PARAMETER_NOT_FOUND(1000),
    /**
     * The access token returned by posting <a href="https://login.live.com/oauth20_token.srf">Microsoft</a> is not found
     */
    MICROSOFT_TOKEN_NOT_FOUND(1001),
    /**
     * The xbox live token returned by posting <a href="https://user.auth.xboxlive.com/user/authenticate">Xbox</a> is not found
     */
    XBOX_LIVE_TOKEN_NOT_FOUND(1002),
    /**
     * The xsts token returned by posting <a href="https://xsts.auth.xboxlive.com/xsts/authorize">XSTS</a> is not found
     */
    XSTS_TOKEN_NOT_FOUND(1003),
    /**
     * The access token returned by posting <a href="https://api.minecraftservices.com/launcher/login">Minecraft Service</a> is not found
     */
    MICROSOFT_ACCESS_TOKEN_NOT_FOUND(1004),
    /**
     * Logged in account not purchased Minecraft
     */
    NOT_OWN_MINECRAFT(1005),
    /**
     * The profile returned by getting <a href="https://api.minecraftservices.com/minecraft/profile">Minecraft Service</a> is invalid
     */
    INVALID_PROFILE(1006),
    /**
     * The file storing the data does not exist
     */
    CONFIGURATION_NOT_FOUND(2000),
    /**
     * The specified account does not exist in the file
     */
    ACCOUNT_NOT_FOUND(2001),
    /**
     * The specified instance does not exist
     */
    INSTANCE_NOT_FOUND(2002),
    /**
     * Manifest fetched via url is illegal
     */
    MANIFEST_ERROR(3000);

    private final int code;

    ResultType(int code) {
        this.code = code;
    }

    /**
     * Get the result code
     *
     * @return the result code
     */
    public int getCode() {
        return code;
    }
}
