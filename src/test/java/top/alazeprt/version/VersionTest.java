package top.alazeprt.version;

import java.util.List;

public class VersionTest {

    static Version version;

    static {
        try {
            version = ((List<Version>) ((Manifest) new Manifest().reloadData().getData()).getVersionList().getData()).get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
