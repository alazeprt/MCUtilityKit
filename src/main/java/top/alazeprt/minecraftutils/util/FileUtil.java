package top.alazeprt.minecraftutils.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtil {

    public static List<File> searchForJavaw(List<File> roots) {
        List<File> files = new ArrayList<>();
        for (File root : roots) {
            files.addAll(bfs(root));
        }
        return files;
    }

    private static List<File> bfs(File root) {
        List<File> list = new ArrayList<>();
        Queue<File> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            File directory = queue.poll();
            if (!directory.isDirectory()) {
                continue;
            }

            File[] files = directory.listFiles();
            if (files == null) {
                continue;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    queue.offer(file);
                } else if (System.getProperty("os.name").toLowerCase().contains("windows") && file.getName().equals("javaw.exe")) {
                    list.add(file);
                } else if (file.getName().equals("java")) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    public static void searchAndExtractFiles(String jarFilePath, String targetDirectory, String fileExtension) throws IOException {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(fileExtension)) {
                    System.out.println("Extracting file: " + entry.getName());
                    extractFile(jarFile, entry, targetDirectory);
                }
            }
        }
    }

    private static void extractFile(JarFile jarFile, JarEntry entry, String targetDirectory) throws IOException {
        File targetDir = new File(targetDirectory);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        try (InputStream inputStream = jarFile.getInputStream(entry)) {
            FileUtils.copyInputStreamToFile(inputStream,
                    new File(targetDirectory, entry.getName().split("/")[entry.getName().split("/").length - 1]));
        }
    }

}
