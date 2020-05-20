package com.github.euler.elasticsearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class ElasticSearchUtils {

    public static RestHighLevelClient initializeClient(Config config) {
        String username;
        try {
            username = config.getString("username");
        } catch (ConfigException.Missing e) {
            username = null;
        }

        String password;
        try {
            password = config.getString("password");
        } catch (ConfigException.Missing e) {
            password = null;
        }

        List<String> hosts = config.getStringList("hosts");

        String ca;
        try {
            ca = config.getString("ssl.certificateAuthorities");
        } catch (ConfigException.Missing e) {
            ca = null;
        }
        return initializeClient(username, password, hosts, ca);
    }

    public static RestHighLevelClient initializeClient(String userName, String password, Collection<String> hosts, String ca) {
        HttpHost[] esHosts = toHttpHosts(hosts);

        RestClientBuilder builder = RestClient.builder(esHosts);

        if (userName != null && !userName.isEmpty() && password != null) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
            builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    if (ca != null) {
                        try {
                            SSLContextBuilder custom = SSLContexts.custom();
                            custom.loadTrustMaterial(loadTrustStore(ca), new TrustSelfSignedStrategy());
                            httpClientBuilder.setSSLContext(custom.build());
                        } catch (GeneralSecurityException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }
        return new RestHighLevelClient(builder);
    }

    public static HttpHost[] toHttpHosts(Collection<String> hosts) {
        return hosts.stream().map(h -> toHttpHost(h)).toArray(HttpHost[]::new);
    }

    public static HttpHost toHttpHost(String uriStr) {
        try {
            URI uri = new URI(uriStr);
            return new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyStore loadTrustStore(String password, String trustStore) throws GeneralSecurityException, IOException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream keystoreStream = ElasticSearchUtils.class.getResourceAsStream(trustStore)) {
            if (password != null) {
                keystore.load(keystoreStream, password.toCharArray());
            } else {
                keystore.load(keystoreStream, null);
            }
        }
        return keystore;
    }

    public static KeyStore loadTrustStore(String trustStore) throws GeneralSecurityException, IOException {
        return loadTrustStore(null, trustStore);
    }

}
