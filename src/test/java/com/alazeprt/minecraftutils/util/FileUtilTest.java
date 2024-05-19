package com.alazeprt.minecraftutils.util;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileUtilTest {
    @Test
    public void searchExecutableFilesTest() {
        System.out.println(Arrays.toString(FileUtil.searchForJavaw(List.of(new File("C:/Program Files"), new File("C:/Program Files (x86)"),
                new File(System.getProperty("user.home")))).toArray()));
    }
}
