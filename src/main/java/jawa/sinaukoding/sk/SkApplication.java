package jawa.sinaukoding.sk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteDataSource;
@SpringBootApplication
@Configuration
public class SkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkApplication.class, args);
	}

	@Bean
	public SQLiteDataSource dataSource() {
		SQLiteDataSource dataSource = new SQLiteDataSource();
		dataSource.setUrl("jdbc:sqlite:D:/Sinau Koding/sksk/tools/db/data/sk.db");
		// Set the busy timeout to 5000 milliseconds (5 seconds)
		dataSource.setBusyTimeout(5000);
		return dataSource;
	}

}
