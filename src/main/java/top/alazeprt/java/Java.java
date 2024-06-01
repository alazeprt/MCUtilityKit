package top.alazeprt.java;

import org.tinylog.Logger;

/**
 * Represents a Java
 *
 * @author alazeprt
 * @version 1.1
 */
public class Java {
    private final String path;
    private final int version;

    /**
     * Create a Java
     *
     * @param path path
     * @param version version
     */
    public Java(String path, int version) {
        Logger.info("Adding Java: " + path + " " + version);
        this.path = path;
        this.version = version;
    }

    /**
     * Get the path of the Java
     *
     * @return the path of the Java
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the version of the Java
     *
     * @return the version of the Java
     */
    public int getVersion() {
        return version;
    }
}
