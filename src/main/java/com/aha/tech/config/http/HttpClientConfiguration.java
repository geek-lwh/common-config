package com.aha.tech.config.http;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * luweihong
 *
 */
@Configuration
@ConditionalOnProperty(name = "use.common.http", matchIfMissing = true)
public class HttpClientConfiguration {

    private final Logger logger = LoggerFactory.getLogger(HttpClientConfiguration.class);

    // 链接目标URI超时时间.
    @Value("${common.http.connect.timeout:5000}")
    private int connectTimeout;

    // 从http pool中获取资源的超时时间.
    @Value("${common.http.request.timeout:5000}")
    private int requestTimeout;

    // 流读取超时时间
    @Value("${common.http.socket.timeout:10000}")
    private int socketTimeout;

    // 最大 http pool链接资源数,根据具体的业务自己定义
    @Value("${common.http.max.total.connections:100}")
    private int maxTotalConnections;

    // keep alive保持长连接不断开的时间
    @Value("${common.http.keep.alive.timeout:5000}")
    private int keepaliveTimeout;

    // 关闭空闲http资源的时间
    @Value("${common.http.close.idle.connection.wait.timeout:5}")
    private int closeIdleConnectionWaitTimeout;

    /**
     * http池化管理,支持https
     * @return
     */
    @Primary
    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            logger.error("http连接池 初始化发生异常,原因是: " + e.getMessage(), e);
        }

        SSLConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(builder.build());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            logger.error("http连接池 初始化发生异常,原因是: " + e.getMessage(), e);
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingConnectionManager.setMaxTotal(maxTotalConnections);

        // 限制每个host最大并发请求数,这里 max = x,则具体某个host为 x/2,客户端根据业务自己配置
        poolingConnectionManager.setDefaultMaxPerRoute(maxTotalConnections / 2);

        logger.info("http连接池初始化完成 最大并发请求数 : {} ,最大连接数", maxTotalConnections / 2, maxTotalConnections);
        return poolingConnectionManager;
    }

    /**
     * 长连接策略的具体配置
     * 会根据response判断是否带有长连接的标识符,如果有则查看是否带有timeout
     * 如果有timeout则以response的值为准,如果没有timeout则取默认值
     * @return
     */
    @Primary
    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();

                if (value != null && param.equalsIgnoreCase("timeout")) {
                    long responseTimeout = Long.parseLong(value);
                    return responseTimeout;
                }
            }

            return -1;
        };
    }

    /**
     * 系统初始化默认的httpclient
     * @return
     */
    @Primary
    @Bean
    public CloseableHttpClient defaultCloseableHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(requestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout).build();

        CloseableHttpClient httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();

        logger.info("http客户端初始化完成 requestTimeout : {} , connectTimeout : {} , socketTimeout : {}", requestTimeout, connectTimeout, socketTimeout);

        return httpClientBuilder;
    }


    /**
     * 10秒一次定时清理闲置的链接和关闭过期的链接
     * @param connectionManager
     * @return
     */
    @Primary
    @Bean
    public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
        return () -> {
            try {
                if (connectionManager != null) {
                    logger.trace("开始清理闲置的链接和关闭过期的链接");
                    connectionManager.closeExpiredConnections();
                    connectionManager.closeIdleConnections(closeIdleConnectionWaitTimeout, TimeUnit.SECONDS);
                } else {
                    logger.warn("初始化http连接池监控线程 不成功");
                }
            } catch (Exception e) {
                logger.error("http连接池监控线程 出现异常. msg={}, e={}", e.getMessage(), e);
            }
        };
    }

}
