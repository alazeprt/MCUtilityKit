package top.alazeprt.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Represents a utility class for file operations
 *
 * @author alazeprt
 * @version 1.1
 */
public class FileUtil {

    /**
     * Search for executable java files in the given roots
     *
     * @param roots folders to search
     * @return found java files
     */
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
                } else if (System.getProperty("os.name").toLowerCase().contains("windows") && file.getName().equals("java.exe")) {
                    list.add(file);
                } else if (file.getName().equals("java")) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**
     * Extract files with a specific extension from a jar file
     *
     * @param jarFilePath path to the jar file
     * @param targetDirectory folder to extract the files
     * @param fileExtension extension of the files
     * @throws IOException if an I/O error occurs
     */
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
