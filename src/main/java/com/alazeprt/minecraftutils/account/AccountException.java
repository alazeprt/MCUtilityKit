package com.alazeprt.minecraftutils.account;

public enum AccountException {
    URL_PARAMETER_NOT_FOUND(0),
    MICROSOFT_TOKEN_NOT_FOUND(1),
    XBOX_LIVE_TOKEN_NOT_FOUND(2),
    XSTS_TOKEN_NOT_FOUND(3),
    MINECRAFT_ACCESS_TOKEN_NOT_FOUND(4),
    NOT_OWN_MINECRAFT(5),
    PROFILE_ERROR(6),
    CANNOT_OPEN_URL(100);

    private final int code;

    AccountException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "AccountException{code=" + code + "}";
    }
}
