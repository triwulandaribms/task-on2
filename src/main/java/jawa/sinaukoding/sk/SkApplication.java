package jawa.sinaukoding.sk;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		System.setProperty("SPRING_APPLICATION_NAME", dotenv.get("SPRING_APPLICATION_NAME"));
		System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
		System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
		System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
		System.setProperty("SPRING_DATASOURCE_DRIVER-CLASS-NAME", dotenv.get("SPRING_DATASOURCE_DRIVER-CLASS-NAME"));

		System.setProperty("SPRING_JPA_DATABASE-PLATFORM", dotenv.get("SPRING_JPA_DATABASE-PLATFORM"));
		System.setProperty("SPRING_JPA_HIBERNATE_DDL-AUTO", dotenv.get("SPRING_JPA_HIBERNATE_DDL-AUTO"));
		System.setProperty("SPRING_JPA_SHOW-SQL", dotenv.get("SPRING_JPA_SHOW-SQL"));

		System.setProperty("SPRING_FLYWAY_ENABLED", dotenv.get("SPRING_FLYWAY_ENABLED"));
		System.setProperty("SPRING_FLYWAY_LOCATIONS", dotenv.get("SPRING_FLYWAY_LOCATIONS"));
		System.setProperty("SPRING_FLYWAY_BASELINE-ON-MIGRATE", dotenv.get("SPRING_FLYWAY_BASELINE-ON-MIGRATE"));

		System.setProperty("SK_JWT_KEY", dotenv.get("SK_JWT_KEY"));

		SpringApplication.run(SkApplication.class, args);
	}
}
