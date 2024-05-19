package com.alazeprt.minecraftutils.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class JavaVersionUtil {
    public static String getJavaVersion(String filePath) {
        File javawFile = new File(filePath);

        try {
            FileInputStream fis = new FileInputStream(javawFile);
            byte[] buffer = new byte[20];

            int bytesRead = fis.read(buffer, 0, buffer.length);

            fis.close();

            if (bytesRead > 0) {
                StringBuilder hexBuilder = new StringBuilder(bytesRead * 2);
                for (int i = 0; i < bytesRead; i++) {
                    hexBuilder.append(String.format("%02x", buffer[i]));
                }
                String hexString = hexBuilder.toString();

                String version = extractVersion(hexString);
                return version;
            } else {
                System.out.println("Failed to read the file.");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractVersion(String hexString) {
        String versionHex = hexString.substring(120, 128);

        int versionInt = Integer.parseInt(versionHex, 16);
        int major = (versionInt >> 8) & 0xFF;
        int minor = versionInt & 0xFF;

        return major + "." + minor;
    }
}
