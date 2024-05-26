package top.alazeprt.util;

import java.util.UUID;

/**
 * Represent the utility class of uuid
 *
 * @author alazeprt
 * @version 1.1
 */
public class UUIDUtil {
    /**
     * Make uuid strings concatenated with a - sign
     *
     * @param input the origin uuid
     * @return the formatted uuid
     */
    public static UUID formatUuid(String input) {
        if(input==null||input.length()!=32) {
            throw new IllegalArgumentException("Invalid UUID string");
        }
        StringBuilder sb=new StringBuilder(input);
        sb.insert(8,'-');
        sb.insert(13,'-');
        sb.insert(18,'-');
        sb.insert(23,'-');
        return UUID.fromString(sb.toString());
    }

    /**
     * Make uuid strings not concatenated with a - sign
     *
     * @param uuid the formatted uuid
     * @return the origin uuid
     */
    public static String unformatUuid(UUID uuid) {
        String uuidString = uuid.toString();
        return uuidString.replaceAll("-", "");
    }
}
