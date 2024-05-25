package top.alazeprt.minecraftutils.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileUtilTest {
    @Test
    @Disabled
    public void searchExecutableFilesTest() {
        System.out.println(Arrays.toString(FileUtil.searchForJavaw(List.of(new File("C:/Program Files"), new File("C:/Program Files (x86)"),
                new File(System.getProperty("user.home")))).toArray()));
    }
}
