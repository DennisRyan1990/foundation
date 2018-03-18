package com.zchi.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;

public class HttpClientUtil {
    private static int TIMEOUT = 120000;
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String APPLICATION_JSON = "application/json;charset=utf-8";
    private static final String APPLICATION_XML = "application/xml;charset=utf-8";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    private static CloseableHttpClient httpClient = null;
    private static final String CONTENT_CHARSET = "UTF-8";
    private static SSLConnectionSocketFactory socketFactory;

    private HttpClientUtil() {
    }

    public static String executeHttpPost(String url, Map<String, String> paramMap) {
        return execute(url, paramMap, null, POST,
            "application/x-www-form-urlencoded; text/html;charset=UTF-8");
    }

    public static String execute(String url, Map<String, String> paramMap, Charset charset,
        HttpMethod method, String contentType) {
        String result = null;
        CloseableHttpResponse response = null;
        if (charset == null) {
            charset = Consts.UTF_8;
        }
        if (method == null) {
            method = HttpMethod.POST;
        }
        HttpRequestBase target = null;
        try {
            switch (method) {
                case GET:
                    StringBuilder sb = new StringBuilder(url);
                    // ?method=save&title=435435435&timelength=89&
                    if (paramMap != null && paramMap.size() > 0) {
                        sb.append('?');
                        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                            if (StringUtils
                                .isNoneBlank(entry.getValue())) {
                                sb.append(entry.getKey()).append('=')
                                    .append(URLEncoder.encode(entry.getValue(), CONTENT_CHARSET))
                                    .append('&');
                            }
                        }
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    target = new HttpGet(sb.toString());
                    break;
                case POST:
                    target = new HttpPost(url);
                    target.setHeader("Content-type",
                        "application/x-www-form-urlencoded; text/html;charset=UTF-8");
                    target.setConfig(requestConfig);
                    if (paramMap != null && paramMap.size() > 0) {
                        List<NameValuePair> list = Lists.newArrayList();
                        for (Map.Entry<String, String> mp : paramMap.entrySet()) {
                            if (StringUtils.isNoneBlank(mp.getValue())) {
                                NameValuePair pair =
                                    new BasicNameValuePair(mp.getKey(), mp.getValue());
                                list.add(pair);
                            }
                        }
                        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                        ((HttpPost) target).setEntity(entity);
                    }
                    break;
                default:
                    logger.warn("the method {} is not supported", method);
                    break;
            }
            if (target != null) {
                target.setHeader("Content-type", contentType);
                target.setConfig(requestConfig);
                response = httpClient.execute(target);
                if (response != null) {
                    HttpEntity httpEntity = response.getEntity();
                    result = EntityUtils.toString(httpEntity, charset);
                }
            }
        } catch (ClientProtocolException e) {
            logger.error("ClientProtocolException", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (org.apache.http.conn.ConnectTimeoutException e) {
            logger.error("ConnectTimeoutException", e);
        } catch (SocketTimeoutException e) {
            logger.error("SocketTimeoutException", e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            target.releaseConnection();
        }
        return result;
    }

    public static String sendPostXml(String uri, String xmlStr, String userName, String password) {
        String result = null;
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setDefaultRequestConfig(RequestConfig.custom()
            .build());
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (uri.toLowerCase().startsWith("https")) {
            try {
                SSLContext sslContext = org.apache.http.conn.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustStrategy() {
                        @Override
                        public boolean isTrusted(
                            final X509Certificate[] chain,
                            final String authType)
                            throws CertificateException {
                            return true;
                        }
                    }).useTLS().build();

                httpClientBuilder.setSslcontext(sslContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        httpClientBuilder.setDefaultRequestConfig(RequestConfig.custom()
            .build());
        CloseableHttpClient client = httpClientBuilder
            .setDefaultCredentialsProvider(credentialsProvider).build();
        HttpPost post = new HttpPost(uri);
        post.setHeader("Content-type", "application/xml;charset=UTF-8");
        post.setHeader("User-Agent","Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:41.0) Gecko/20100101 Firefox/41.0");
        StringEntity se = new StringEntity(xmlStr,"utf-8");
        post.setEntity(se);
        try{
            HttpResponse response = client.execute(post);
            result = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            logger.info("sendPostXml request,uri:{},params:{},result:{}", uri, xmlStr, result);
        }catch(Exception e){
            logger.error("remote request error, uri -> "+uri, e);
        }
        return result;
    }

    private static RequestConfig requestConfig = RequestConfig.custom()
        .setCookieSpec(CookieSpecs.STANDARD_STRICT)
        .setExpectContinueEnabled(true)
        .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
        .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
        .build();

    private static TrustManager manager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    static{
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{manager}, null);
            socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", socketFactory).build();
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
