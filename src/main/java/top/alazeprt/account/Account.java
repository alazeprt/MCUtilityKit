package top.alazeprt.account;

import java.util.UUID;

/**
 * Represents an account.
 *
 * @author alazeprt
 * @version 1.1
 */
public interface Account {
    /**
     * Retrieves the name of the account.
     *
     * @return the name of the account
     */
    String getName();

    /**
     * Retrieves the UUID of the account.
     *
     * @return the UUID of the account
     */
    UUID getUuid();

    /**
     * Retrieves the type of the account.
     *
     * @return the type of the account
     */
    AccountType getType();
}
