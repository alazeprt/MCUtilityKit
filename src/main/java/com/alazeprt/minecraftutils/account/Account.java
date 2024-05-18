package com.alazeprt.minecraftutils.account;

import java.util.UUID;

public interface Account {
    String getName();
    UUID getUuid();
    AccountType getType();
}
