package th.co.infinitait.goodluck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import th.co.infinitait.goodluck.config.GracefulShutdown;

import javax.annotation.PostConstruct;
import java.util.Locale;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"th.co.infinitait"})
public class GoodLuckApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoodLuckApplication.class, args);
	}

	@PostConstruct
	public void setDefaultLocale() {
		Locale.setDefault(Locale.US);
	}

	@Bean
	public GracefulShutdown gracefulShutdown() {
		return new GracefulShutdown();
	}

	@Bean
	public ConfigurableServletWebServerFactory webServerFactory(final GracefulShutdown gracefulShutdown) {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addConnectorCustomizers(gracefulShutdown);
		return factory;
	}
}
