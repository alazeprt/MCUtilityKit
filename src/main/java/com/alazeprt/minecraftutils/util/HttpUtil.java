package com.alazeprt.minecraftutils.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.net.URIBuilder;

import com.google.gson.Gson;

import org.apache.hc.core5.http.NameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpUtil {
    public static String sendPost(String url, Map<String, Object> data, Map<String, String> headers, boolean encodeUrl) throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                post.setHeader(header.getKey(), header.getValue());
            }
            if (data != null) {
                if(encodeUrl) {
                    List<NameValuePair> nvps = new ArrayList<>();
                    data.forEach((key, value) -> {
                        nvps.add(new BasicNameValuePair(key, value.toString()));
                    });
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);
                    post.setEntity(entity);
                } else {
                    Gson gson = new Gson();
                    StringEntity entity = new StringEntity(gson.toJson(data));
                    post.setEntity(entity);
                }
            }
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                return EntityUtils.toString(responseEntity);
            }
        }
        return null;
    }

    public static void download(String url, String path, int thread) throws IOException, InterruptedException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        long fileSize = Long.parseLong(response.getFirstHeader("Content-Length").getValue());

        // 计算每个线程需要下载的文件大小
        long partSize = fileSize / thread;

        ExecutorService executor = Executors.newFixedThreadPool(thread);
        for (int i = 0; i < thread; i++) {
            long startByte = i * partSize;
            long endByte = (i == thread - 1) ? fileSize - 1 : (i + 1) * partSize - 1;
            executor.execute(new DownloadThread(url, path, startByte, endByte));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        httpClient.close();
    }

    public static String sendGet(String url, Map<String, Object> data, Map<String, String> headers) throws IOException, ParseException, URISyntaxException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (data != null) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
                }
            }
            HttpGet get = new HttpGet(uriBuilder.build());
            for (Map.Entry<String, String> header : headers.entrySet()) {
                get.setHeader(header.getKey(), header.getValue());
            }
            CloseableHttpResponse response = httpClient.execute(get);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                return EntityUtils.toString(responseEntity);
            }
        }
        return null;
    }

    static class DownloadThread implements Runnable {

        private final String url;
        private final String path;
        private final long startByte;
        private final long endByte;

        public DownloadThread(String url, String path, long startByte, long endByte) {
            this.url = url;
            this.path = path;
            this.startByte = startByte;
            this.endByte = endByte;
        }

        @Override
        public void run() {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("Range", "bytes=" + startByte + "-" + endByte);
                try (InputStream in = httpClient.execute(httpGet).getEntity().getContent();
                     RandomAccessFile out = new RandomAccessFile(new File(path), "rw")) {
                    out.seek(startByte);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

