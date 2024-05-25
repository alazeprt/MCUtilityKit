package top.alazeprt.minecraftutils.util;

import com.google.gson.Gson;

import java.util.Map;

public enum Result {
    SUCCESS(0),
    FILE_IO_EXCEPTION(100),
    NETWORK_IO_EXCEPTION(101),
    ALGORITHM_EXCEPTION(102),
    JSON_SYNTAX_EXCEPTION(103),
    SHA1_NOT_MATCH(104),
    INDETERMINATE(105),
    URL_PARAMETER_NOT_FOUND(1000),
    MICROSOFT_TOKEN_NOT_FOUND(1001),
    XBOX_LIVE_TOKEN_NOT_FOUND(1002),
    XSTS_TOKEN_NOT_FOUND(1003),
    MICROSOFT_ACCESS_TOKEN_NOT_FOUND(1004),
    NOT_OWN_MINECRAFT(1005),
    INVALID_PROFILE(1006),;

    private final int code;

    private Object data;

    Result(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(Map.of("code", code, "message", this.toString()));
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    public Object getData() {
        return data;
    }
}
