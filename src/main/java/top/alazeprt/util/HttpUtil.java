package top.alazeprt.util;

import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static boolean sha1verify(File path, String origin_sha1) throws NoSuchAlgorithmException, IOException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        FileInputStream fis = new FileInputStream(path);

        byte[] data = new byte[1024];
        int read;
        while ((read = fis.read(data)) != -1) {
            sha1.update(data, 0, read);
        }
        fis.close();

        byte[] hashBytes = sha1.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return origin_sha1.contentEquals(sb);
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
}

