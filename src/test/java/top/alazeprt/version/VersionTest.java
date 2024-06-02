package top.alazeprt.version;

import top.alazeprt.util.Result;
import top.alazeprt.util.ResultType;

import java.util.List;

public class VersionTest {

    static Version version;

    static {
        try {
            Result<Manifest> result = new Manifest().reloadData();
            List<Version> versionList = result.getData().getVersionList().getData();
            version = versionList.get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
