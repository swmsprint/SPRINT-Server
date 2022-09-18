package sprint.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import sprint.server.domain.Running;

import java.sql.Timestamp;
import java.util.StringTokenizer;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@PropertySource(value = {"classpath:jdbc.properties"})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
