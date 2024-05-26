package top.alazeprt.version;

/**
 * Represents a version
 *
 * @author alazeprt
 * @version 1.1
 *
 * @param version the version name
 * @param type the type of the version
 * @param url the manifest url of the version
 * @param releaseTime the release time of the version
 */
public record Version(String version, VersionType type, String url, String releaseTime) {
    @Override
    public String toString() {
        return "Version{" +
                "version='" + version + '\'' +
                ", type=" + type +
                ", url='" + url + '\'' +
                ", releaseTime='" + releaseTime + '\'' +
                '}';
    }
}
