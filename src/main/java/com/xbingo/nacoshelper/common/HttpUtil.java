package com.xbingo.nacoshelper.common;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;

/** HttpUtil
 * @author xubin
 */
public class HttpUtil {

    private final Duration timeout = Duration.ofSeconds(5);
    private final byte[] lock = new byte[0];
    private volatile HttpClient httpClient = null;

    private HttpUtil(){
        if (httpClient == null){
            synchronized (lock){
                if (httpClient == null){
                    httpClient = HttpClient.newBuilder()
                            .version(HttpClient.Version.HTTP_2)
                            .connectTimeout(timeout)
                            .followRedirects(HttpClient.Redirect.NEVER)
                            .sslContext(sslContext())
                            .proxy(ProxySelector.getDefault())
                            .build();
                }
            }
        }
    }

    /**
     * 创建 HttpUtil
     * @return
     */
    public static HttpUtil builder(){
        return new HttpUtil();
    }

    /**
     * get请求
     * @param url 地址
     * @return
     */
    public String get(String url){
        String body = "";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_2)
                    .uri(URI.create(url))
                    .GET()
                    .timeout(timeout)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                body = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * post请求
     * @param url 地址
     * @param data json字符串
     * @return
     */
    public String post(String url,String data){
        String body = "";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type","application/x-www-form-urlencoded")
                    .version(HttpClient.Version.HTTP_2)
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(data))
                    .timeout(timeout)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                body = response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     * @return
     */
    private SSLContext sslContext(){
        TrustManager[] trustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}
                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustManagers, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sc;
    }
}