package top.alazeprt.minecraftutils.version;

public class VersionTest {

    static Version version;

    static {
        try {
            version = ((Manifest) new Manifest().reloadData().getData()).getVersionList().get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
