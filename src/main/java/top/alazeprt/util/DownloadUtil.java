package top.alazeprt.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents a utility class for downloading files
 *
 * @author alazeprt
 * @version 1.1
 */
public class DownloadUtil {

    /**
     * Download a file using multiple threads
     *
     * @param url the url of the file
     * @param path the path of the file
     * @param threadCount the number of threads to download
     * @throws InterruptedException if the thread is interrupted
     * @throws IOException if an I/O error occurs
     * @throws RuntimeException if an error occurs
     */
    public static void multi(String url, String path, int threadCount) throws InterruptedException, IOException, RuntimeException {
        URL downloadUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
        int fileSize = connection.getContentLength();
        connection.disconnect();

        int partSize = fileSize / threadCount;
        int lastPartSize = partSize + fileSize % threadCount;

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            int startByte = i * partSize;
            int endByte = (i == threadCount - 1) ? startByte + lastPartSize - 1 : startByte + partSize - 1;
            threads[i] = new DownloadThread(url, path, startByte, endByte);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Download completed successfully.");
    }

    private static class DownloadThread extends Thread {
        private String url;
        private String path;
        private int startByte;
        private int endByte;

        public DownloadThread(String url, String path, int startByte, int endByte) {
            this.url = url;
            this.path = path;
            this.startByte = startByte;
            this.endByte = endByte;
        }

        @Override
        public void run() {
            try {
                URL downloadUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
                InputStream inputStream = connection.getInputStream();

                RandomAccessFile outputStream = new RandomAccessFile(path, "rw");
                outputStream.seek(startByte);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Downloaded bytes " + startByte + " to " + endByte);
        }
    }

    /**
     * Download a file using a single thread
     *
     * @param url the url of the file
     * @param path the path of the file
     * @throws IOException if an I/O error occurs
     */
    public static void single(String url, String path) throws IOException {
        URL downloadUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
        connection.disconnect();

        InputStream inputStream = downloadUrl.openStream();
        FileOutputStream outputStream = new FileOutputStream(path);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();

        System.out.println("Download completed successfully.");
    }
}
