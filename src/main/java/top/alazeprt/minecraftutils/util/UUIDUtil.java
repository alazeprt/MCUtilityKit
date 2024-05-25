package top.alazeprt.minecraftutils.util;

import java.util.UUID;

public class UUIDUtil {
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

    public static String unformatUuid(UUID uuid) {
        String uuidString = uuid.toString();
        return uuidString.replaceAll("-", "");
    }
}
