package com.alazeprt.minecraftutils.account;

import java.util.UUID;

public class OfflineAccount implements Account {
    private final AccountType type = AccountType.OFFLINE;

    private String name;

    private final UUID uuid;

    public OfflineAccount(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public OfflineAccount(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public AccountType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }
}
