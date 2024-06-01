package top.alazeprt.account;

import org.tinylog.Logger;

import java.util.UUID;

/**
 * Represents an offline account
 *
 * @author alazeprt
 * @version 1.1
 */
public class OfflineAccount implements Account {
    private final AccountType type = AccountType.OFFLINE;

    private String name;

    private final UUID uuid;

    /**
     * Constructor for offline account
     *
     * @param name the name of the account
     */
    public OfflineAccount(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        Logger.info("Added offline account: " + name + ", UUID: " + uuid);
    }

    /**
     * Constructor for offline account
     *
     * @param name the name of the account
     * @param uuid the uuid of the account
     */
    public OfflineAccount(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
        Logger.info("Added offline account: " + name + ", UUID: " + uuid);
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

    /**
     * Set the name of the account
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
