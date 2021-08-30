package th.co.infinitait.goodluck.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Getter
@Configuration
public class Properties {

	@Value("${report-location}")
	private Resource storageLocation;
	@Value("${storage-location}")
	private Resource reportLocation;
	@Value("${mail.sender}")
	private String sender;
	@Value("${mail.personal}")
	private String personal;
	@Value("${mail.message-subject}")
	private String messageSubject;
	@Value("${mail.recipients.username}")
	private String username;
	@Value("${mail.recipients.email}")
	private String email;

	@Bean
	public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setProtocol("SMTP");
		javaMailSender.setHost("smtp.gmail.com");
		javaMailSender.setPort(587);
		return javaMailSender;
	}

	@Value("${image.upload.dir}")
	private String imageRootPath;

}
