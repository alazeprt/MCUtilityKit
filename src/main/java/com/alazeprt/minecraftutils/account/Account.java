package com.alazeprt.minecraftutils.account;

import java.util.UUID;

import com.alazeprt.minecraftutils.account.AccountType;

public interface Account {
    String getName();
    UUID getUuid();
    AccountType getType();
}
