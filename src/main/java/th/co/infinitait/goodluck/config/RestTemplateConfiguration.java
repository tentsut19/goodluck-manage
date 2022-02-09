package th.co.infinitait.goodluck.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Configuration
public class RestTemplateConfiguration {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
//        executor.setAwaitTerminationMillis(conf.getAwaitTerminationPeriod());
//        executor.setWaitForTasksToCompleteOnShutdown(conf.isShutdownAwaitTermination());
        executor.setThreadNamePrefix("GoodLuckThread-");
        return executor;
    }

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(getClientHttpRequestFactory()));
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(getHttpClient());
        factory.setConnectTimeout(1000 * 10);
        factory.setReadTimeout(1000 * 5);
        return factory;
    }

    @Bean
    public CloseableHttpClient getHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(10);
        connectionManager.setMaxTotal(100);
        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

}
